package daily_farm.api;

public interface FarmSetApiConstants {

	String ADD_FARM_SET = "/farm-set";
	String GET_ALL_SETS_BY_SIZE = "/farm-set/size";
	String GET_ALL_SETS_BY_CATEGORY = "/farm-set/category";
	String GET_ALL_SETS_BY_FARMER = "/farm-set/farmer";
	String GET_ALL_SETS = "/farm-set/all";
	
	String FARM_SET_DECREASE_STOK_FOR_ORDER = "/farm-set/order";
	String GET_FARM_SET_FOR_ORDER = "/farm-set/set-for-order";
	String FARM_SET_INCREASE_STOK_FOR_ORDER = "/farm-set/cancel_order";
	
	String FARM_SET_ORDER = "/farm-set/order";

	
}
