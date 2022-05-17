package ru.nika.thsearch.DAO;

import java.util.List;
import java.util.Map;

/**
 * @author Marat Sadretdinov
 */
public interface DataCardDAO {
    void insert(DataCard dataCard);
    void delete(DataCard dataCard);
    void update(DataCard dataCard);
    void delete(Integer id);


    void insertErrorsCard(Errors errors);
    void insertSuccessCard(DataCard dataCard);
    void updateApply(Map<String, String> map);

    DataCard getDataCardByID(Integer id);

    List<DataCard> getTableIndex();
    List<DataCard> getDataCardByCar(String car);
    List<DataCard> getDataCardByPlaceOfLoading(String placeOfLoading);
    List<DataCard> getDataCardByPlaceOfDelivery(String placeOfDelivery);
    List<DataCard> getDataCardByShipmentStart(String shipmentStart);
    List<DataCard> getDataCardByShipmentEnd(String shipmentEnd);
    List<DataCard> getDataCardByLoading(String loading);
    List<DataCard> getDataCardByAmount(String amount);

}
