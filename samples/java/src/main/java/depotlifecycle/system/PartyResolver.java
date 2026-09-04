package depotlifecycle.system;

import depotlifecycle.domain.ExternalParty;
import depotlifecycle.domain.Party;
import depotlifecycle.repositories.ExternalPartyRepository;
import depotlifecycle.repositories.PartyRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * Resolves the parties carried on an incoming message to rows in this example application's
 * database, reusing the row already stored for a given identity instead of inserting a new one
 * per transmission.
 *
 * <p>Parties identify a company, not an event, so the same depot or owner arrives on every
 * message about that depot.  Inserting a row per transmission grows the party table without
 * bound - a long running instance accumulates one row for every gate, estimate, release and
 * work order it has ever received, which exhausts the in-memory database this sample ships
 * with.  Reusing the stored row keeps the table proportional to the number of companies.</p>
 *
 * <p>Identity falls back through `companyId`, then `code` (the internal system code), then
 * `name`, taking the first one the message actually supplied.  An external party is the case
 * that needs the fallback: `companyId` is optional there, so a message that omits it (or leaves
 * it at its default, blank, value) identifies the company by its internal code, or by name when
 * it carries neither identifier.</p>
 *
 * <p>The stored row wins when one already exists: this sample treats a party as an identity
 * rather than a versioned record, so contact and address details supplied on a later message
 * do not overwrite it.</p>
 */
@Singleton
@RequiredArgsConstructor
public class PartyResolver {
    private final PartyRepository partyRepository;
    private final ExternalPartyRepository externalPartyRepository;

    /**
     * @param party the party supplied on an incoming message; may be null
     * @return the stored party for this identity, saving the supplied one if it is unknown
     */
    public Party resolve(Party party) {
        if (party == null) {
            return null;
        }

        return find(party).orElseGet(() -> partyRepository.save(party));
    }

    /**
     * Looks up the stored row for a party without saving one when it is unknown, so a lookup
     * keyed off a party (see {@link InventoryUpdater}) uses the same identity this resolver does.
     *
     * @param party the party to identify; may be null
     * @return the stored party for this identity, or empty when it is unknown
     */
    public Optional<Party> find(Party party) {
        if (party == null) {
            return Optional.empty();
        }

        if (identifies(party.getCompanyId())) {
            return partyRepository.findByCompanyId(party.getCompanyId());
        }

        if (identifies(party.getCode())) {
            return partyRepository.findByCode(party.getCode());
        }

        if (identifies(party.getName())) {
            return partyRepository.findByName(party.getName());
        }

        return Optional.empty();
    }

    /**
     * An external party may be identified by either `companyId` or `code`, so a supplied
     * `companyId` is matched against both.  Only an existing external party is reused - a
     * {@link Party} shares this table but represents a company with an established EDI address,
     * so it is not interchangeable with an external one.
     *
     * @param party the external party supplied on an incoming message; may be null
     * @return the stored external party for this identity, saving the supplied one if unknown
     */
    public ExternalParty resolve(ExternalParty party) {
        if (party == null) {
            return null;
        }

        return findExternal(party)
            .filter(found -> found.getClass() == ExternalParty.class)
            .orElseGet(() -> externalPartyRepository.save(party));
    }

    private Optional<ExternalParty> findExternal(ExternalParty party) {
        if (identifies(party.getCompanyId())) {
            return externalPartyRepository.findByCompanyIdOrCode(party.getCompanyId(), party.getCode());
        }

        if (identifies(party.getCode())) {
            return externalPartyRepository.findByCode(party.getCode());
        }

        if (identifies(party.getName())) {
            return externalPartyRepository.findByName(party.getName());
        }

        return Optional.empty();
    }

    /**
     * A supplied value only identifies a company when it actually carries text - a blank
     * `companyId` is as unusable as a missing one, and matching on it would collapse every party
     * that left it out onto a single stored row.
     */
    private static boolean identifies(String identifier) {
        return identifier != null && !identifier.isBlank();
    }
}
