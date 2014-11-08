package pl.allegro.devfest;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class BestDealAdvisor {

    private final Collection<Auction> auctions;

    public BestDealAdvisor(Collection<Auction> auctions) {
        this.auctions = Collections.unmodifiableCollection(auctions);
    }

    public Optional<BestDeal> findBestDeal(DealCriteria dealRequirements) {
        Collection<Optional<Auction>> auctionsForRequirements =
           dealRequirements
          .getGoodTypes()
          .stream()
          .map(goodType -> findCheapestAuction(goodType))
          .collect(Collectors.toList());

        if (auctionsForRequirements.stream().anyMatch(optional -> !optional.isPresent()))
          return Optional.empty();

        Collection<Auction> auctions = auctionsForRequirements
          .stream()
          .map(optional -> optional.get())
          .collect(Collectors.toList());

        BestDeal bestDeal = new BestDeal(auctions);

        return bestDeal.getTotalPrice().compareTo(dealRequirements.getBudget()) > 0
          ? Optional.empty()
          : Optional.of(bestDeal);
    }

    private Optional<Auction> findCheapestAuction(GoodType goodType) {
        return auctions
          .stream()
          .filter(auction -> auction.getGoodType() == goodType)
          .sorted((a1, a2) -> a1.getPrice().compareTo(a2.getPrice()))
          .findFirst();
    }
}
