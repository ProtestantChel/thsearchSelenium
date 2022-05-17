package ru.nika.thsearch.config;

import org.springframework.stereotype.Component;
import ru.nika.thsearch.DAO.DataCard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marat Sadretdinov
 */
@Component
public class PublicCard {
    private List<DataCard> dataCards;


    public void setDataCard(DataCard dataCard) {
        this.dataCards.add(dataCard);
    }

    public PublicCard(){

    }
    public PublicCard(List<DataCard> dataCards) {
        this.dataCards = dataCards;
    }

    public List<DataCard> getDataCards() {
        return dataCards;
    }

    public void setDataCards(List<DataCard> dataCards) {
        this.dataCards = dataCards;
    }
}
