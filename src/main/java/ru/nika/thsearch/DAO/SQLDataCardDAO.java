package ru.nika.thsearch.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.nika.thsearch.config.PublicCard;
import ru.nika.thsearch.selenium.RowsLast;
import ru.nika.thsearch.selenium.TdValue;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author Marat Sadretdinov
 */
@Component("sqlDataCardDAO")
public class SQLDataCardDAO implements DataCardDAO{
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private PublicCard publicCard;

    @Autowired
    private TdValue tdValue;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<DataCard> getTableIndex(){
        String sql = "SELECT t1.id, car, numTask, placeOfLoading, placeOfDelivery, onPlaceOfDelivery, num, shipmentStart, shipmentEnd, loading, amount, chk_apply, chk_search, col_check FROM (SELECT card.id as id, car, placeOfLoading as placeOfLoading, placeOfDelivery as placeOfDelivery, onPlaceOfDelivery as onPlaceOfDelivery, num as num, shipmentStart, shipmentEnd, loading, amount, chk_apply, chk_search FROM card, card_chk_search WHERE card.id=card_chk_search.id_card ORDER BY card.id DESC) as t1 LEFT OUTER JOIN card_attr on t1.id=card_attr.id_card";
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        List<DataCard> dataCardList = jdbcTemplate.query(sql,sqlParameterSource,new CardRowMapper());
        dataCardList.stream()
                .filter(e -> {
                    try {
                        tdValue.getRowsLasts().get(e.getId().toString());
                        return true;
                    }catch (NullPointerException exception){
                        return false;
                    }
                })
                .forEach(e -> e.setRowsLasts(tdValue.getRowsLasts()));
//        dataCardList.stream()
//                .map(e -> {
//                    try {
//                        System.out.println(e.getId() + " " + tdValue.getRowsLasts().get(e.getId().toString()).get(0).getPlaceOfLoading());
//                        return e.getId() + " " + e.getRowsLasts().values();
//                    }catch (NullPointerException nullPointerException){
//                        return e.getId();
//                    }
//                })
//                .forEach(System.out::println);
        return dataCardList;
    }
    public List<DataCard> getTableSearch(){
        String sql = "SELECT t1.id, car, numTask, placeOfLoading, placeOfDelivery, onPlaceOfDelivery, num, shipmentStart, shipmentEnd, loading, amount, chk_apply, chk_search, col_check FROM (SELECT card.id as id, car, placeOfLoading as placeOfLoading, placeOfDelivery as placeOfDelivery, onPlaceOfDelivery as onPlaceOfDelivery, num as num, shipmentStart, shipmentEnd, loading, amount, chk_apply, chk_search FROM card, card_chk_search WHERE card.id=card_chk_search.id_card ORDER BY card.id DESC) as t1 LEFT OUTER JOIN card_attr on t1.id=card_attr.id_card where col_check = 1550 and chk_search = 1554";
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        return jdbcTemplate.query(sql,sqlParameterSource,new CardRowMapper());
    }
    public Mono<DataCard> insertMono(DataCard dataCard){
        return Mono.just(dataCard);
    }
    @Override
    public void insert(DataCard dataCard) {
        String sql = "insert into card (car, placeOfLoading, placeOfDelivery, shipmentStart, shipmentEnd, loading, amount, num, onPlaceOfDelivery) VALUES (:car, :placeOfLoading, :placeOfDelivery, :shipmentStart, :shipmentEnd, :loading, :amount, :num, :onPlaceOfDelivery);";
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("car", dataCard.getCar());
        sqlParameterSource.addValue("placeOfLoading", dataCard.getPlaceOfLoading());
        sqlParameterSource.addValue("placeOfDelivery", dataCard.getPlaceOfDelivery());
        sqlParameterSource.addValue("shipmentStart", dataCard.getShipmentStart());
        sqlParameterSource.addValue("shipmentEnd", dataCard.getShipmentEnd());
        sqlParameterSource.addValue("loading", dataCard.getLoading());
        sqlParameterSource.addValue("amount", dataCard.getAmount());
        String num = dataCard.getNum().equals("") ? null : dataCard.getNum();
        sqlParameterSource.addValue("num", num);
        sqlParameterSource.addValue("onPlaceOfDelivery", dataCard.getOnPlaceOfDelivery());
        jdbcTemplate.update(sql, sqlParameterSource);
        publicCard.setDataCards(this.getTableSearch());
    }
    @Override
    public void delete(Integer id){
        String sql = "delete from card where id=:id";

        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("id",id);
        jdbcTemplate.update(sql,sqlParameterSource);
        sql = "delete from card_attr where id_card=:id";
        sqlParameterSource.addValue("id",id);
        jdbcTemplate.update(sql,sqlParameterSource);
        sql = "delete from card_chk_search where id_card=:id";
        sqlParameterSource.addValue("id",id);
        jdbcTemplate.update(sql,sqlParameterSource);
    }

    @Override
    public void delete(DataCard dataCard) {
        delete(dataCard.getId());
        publicCard.setDataCards(this.getTableSearch());
    }

    @Override
    public void update(DataCard dataCard) {
        String sql = "UPDATE card set car=:car, placeOfLoading=:placeOfLoading, placeOfDelivery=:placeOfDelivery, " +
                "shipmentStart=:shipmentStart, shipmentEnd=:shipmentEnd, loading=:loading, amount=:amount, num=:num, onPlaceOfDelivery=:onPlaceOfDelivery WHERE id=:id";

        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();

        sqlParameterSource.addValue("car", dataCard.getCar());
        sqlParameterSource.addValue("placeOfLoading", dataCard.getPlaceOfLoading());
        sqlParameterSource.addValue("placeOfDelivery", dataCard.getPlaceOfDelivery());
        sqlParameterSource.addValue("shipmentStart", dataCard.getShipmentStart());
        sqlParameterSource.addValue("shipmentEnd", dataCard.getShipmentEnd());
        sqlParameterSource.addValue("loading", dataCard.getLoading());
        sqlParameterSource.addValue("amount", dataCard.getAmount());
        sqlParameterSource.addValue("id", dataCard.getId());
        String num = dataCard.getNum().equals("") ? null : dataCard.getNum();
        sqlParameterSource.addValue("num", num);
        sqlParameterSource.addValue("onPlaceOfDelivery", dataCard.getOnPlaceOfDelivery());
        jdbcTemplate.update(sql, sqlParameterSource);


        sql = "UPDATE card_attr set col_check=1550, numTask=null WHERE id_card=:id";
        jdbcTemplate.update(sql, sqlParameterSource);

        sql = "UPDATE card_chk_search  set chk_search=1555, chk_apply=1559 WHERE id_card=:id";
        jdbcTemplate.update(sql, sqlParameterSource);
        publicCard.setDataCards(this.getTableSearch());
    }
    @Override
    public void updateApply(Map<String, String> map){
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("id", Integer.parseInt(map.get("id")));

        String sql = "UPDATE card_chk_search set ";
        if (!map.get("chk_search").equals("")) {
            sql = sql + "chk_search=:chk_search";
            sqlParameterSource.addValue("chk_search", Integer.parseInt(map.get("chk_search")));
        }
        if (!map.get("chk_search").equals("") && !map.get("chk_apply").equals("")) sql = sql + " , ";
        if (!map.get("chk_apply").equals("")) {
            sql = sql + "chk_apply=:chk_apply";
            sqlParameterSource.addValue("chk_apply", Integer.parseInt(map.get("chk_apply")));
        }
        sql = sql + " WHERE id_card=:id";


        jdbcTemplate.update(sql, sqlParameterSource);
        publicCard.setDataCards(this.getTableSearch());
    }
    @Override
    public void insertSuccessCard(DataCard dataCard){
        String sql = "UPDATE card_attr set col_check=1551, numTask=:numTask WHERE id_card=:id_card";
        System.out.println("DATACARD = " + dataCard.getId() + " " + dataCard.getNumTask());
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("id_card", dataCard.getId());
        sqlParameterSource.addValue("numTask", dataCard.getNumTask());
        jdbcTemplate.update(sql, sqlParameterSource);
    }

    @Override
    public void insertErrorsCard(Errors errors){
        String sql = "insert into errors_list (id_card, error, DATE ) VALUES (:id_card, :error, (SELECT date('now')))";

        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("id_card", errors.getId());
        sqlParameterSource.addValue("error", errors.getError());
        jdbcTemplate.update(sql, sqlParameterSource);

        sql = "insert into card_attr (id_card, col_check, numTask) VALUES (:id_card, 1551, '')";
        MapSqlParameterSource sqlParameterSource1 = new MapSqlParameterSource();
        sqlParameterSource1.addValue("id_card", errors.getId());
        jdbcTemplate.update(sql, sqlParameterSource1);
    }

    @Override
    public DataCard getDataCardByID(Integer id) {
        return null;
    }

    @Override
    public List<DataCard> getDataCardByCar(String car) {
        return null;
    }

    @Override
    public List<DataCard> getDataCardByPlaceOfLoading(String placeOfLoading) {
        return null;
    }

    @Override
    public List<DataCard> getDataCardByPlaceOfDelivery(String placeOfDelivery) {
        return null;
    }

    @Override
    public List<DataCard> getDataCardByShipmentStart(String shipmentStart) {
        return null;
    }

    @Override
    public List<DataCard> getDataCardByShipmentEnd(String shipmentEnd) {
        return null;
    }

    @Override
    public List<DataCard> getDataCardByLoading(String loading) {
        return null;
    }

    @Override
    public List<DataCard> getDataCardByAmount(String amount) {
        return null;
    }

    private static final class CardRowMapper implements RowMapper<DataCard> {
        @Override
        public DataCard mapRow(ResultSet resultSet, int rowNum) throws SQLException{
            DataCard dataCard = new DataCard();
            dataCard.setId(resultSet.getInt("id"));
            dataCard.setCar(resultSet.getString("car"));
            dataCard.setNumTask(resultSet.getString("numTask"));
            dataCard.setPlaceOfLoading(resultSet.getString("placeOfLoading"));
            dataCard.setPlaceOfDelivery(resultSet.getString("placeOfDelivery"));
            dataCard.setOnPlaceOfDelivery(resultSet.getString("onPlaceOfDelivery"));
            dataCard.setShipmentStart(resultSet.getString("shipmentStart"));
            dataCard.setShipmentEnd(resultSet.getString("shipmentEnd"));
            dataCard.setLoading(resultSet.getString("loading"));
            dataCard.setAmount(resultSet.getString("amount"));
            dataCard.setNum(resultSet.getString("num"));
            dataCard.setChk_apply(resultSet.getInt("chk_apply"));
            dataCard.setChk_search(resultSet.getInt("chk_search"));
            dataCard.setCol_check(resultSet.getInt("col_check"));
            return dataCard;
        }
    }
}
