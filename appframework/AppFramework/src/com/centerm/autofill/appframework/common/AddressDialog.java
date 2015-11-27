package com.centerm.autofill.appframework.common;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.R.integer;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.centerm.autofill.appframework.R;
import com.centerm.autofill.appframework.domain.CityModel;
import com.centerm.autofill.appframework.domain.DistrictModel;
import com.centerm.autofill.appframework.domain.ProvinceModel;
import com.centerm.autofill.appframework.service.XmlParserHandler;


/**
 * 提示对话框
 * 
 * 
 */
public class AddressDialog extends Dialog  
		implements OnClickListener, OnWheelChangedListener {
	
	private static final String TAG = "AddressDialog";
	
	private Context mContext;
	private LayoutInflater mInflater;
	private AddressListener listener;

	private WheelView mViewProvince;
	private WheelView mViewCity;
	private Button mOk;
	private Button mCancel;	

	
	/**
	 * 所有省
	 */
	protected String[] mProvinceDatas;
	/**
	 * key - 省 value - 市
	 */
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();

	/**
	 * 当前省的名称
	 */
	protected String mCurrentProviceName;
	/**
	 * 当前市的名称
	 */
	protected String mCurrentCityName;

	
	 /** 
     * 自定义Dialog监听器 
     */  
    public interface AddressListener {  
        /** 
         * 回调函数，用于在Dialog的监听事件触发后刷新Activity的UI显示 
         */  
        public void onSelectAddr(String provice, String city);  
    }
    
    
	public AddressDialog(Context context) {
		super(context, R.style.dialog_style);
		mContext = context;
		
	}
	public AddressDialog(Context context,String proviceName) {
		super(context, R.style.dialog_style);
		mContext = context;
		this.mCurrentProviceName = proviceName;
		
	}
	private AddressDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
		
	}

	private void init()
	{
		setContentView(R.layout.dialog_address);
		
		mInflater = (LayoutInflater) mContext.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		
		setUpViews();
		setUpListener();
		setUpData();
		
		if(mCurrentProviceName!=null){
			setCurrentItemProvince(mCurrentProviceName);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		init();
	}
	
	private void setUpViews() {
		mViewProvince = (WheelView) findViewById(R.id.id_province);
		mViewCity = (WheelView) findViewById(R.id.id_city);

		mOk = (Button) findViewById(R.id.btOk);
		mCancel = (Button) findViewById(R.id.btCancel);
	}
	
	private void setUpListener() {
    	// 添加change事件
    	mViewProvince.addChangingListener(this);
    	// 添加change事件
    	mViewCity.addChangingListener(this);

    	// 添加onclick事件
    	mOk.setOnClickListener(this);
    	mCancel.setOnClickListener(this);
    }
	
	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mProvinceDatas));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);

		updateCities();
		updateAreas();
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// TODO Auto-generated method stub
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} 
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(mContext, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btOk) {
			showSelectedResult();
			if(listener != null) {
				listener.onSelectAddr(mCurrentProviceName, mCurrentCityName);
			}
			dismiss();
		} else if (id == R.id.btCancel) {
			dismiss();
		} else {
		}
	}
	
	/**
	 * 解析省市区的XML数据
	 */
	
    protected void initProvinceDatas()
	{
		List<ProvinceModel> provinceList = null;
    	AssetManager asset = mContext.getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// 解析xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			// 获取解析出来的数据
			provinceList = handler.getDataList();
			//*/ 初始化默认选中的省、市、区
			if (provinceList!= null && !provinceList.isEmpty()) {
				mCurrentProviceName = provinceList.get(0).getName();
				List<CityModel> cityList = provinceList.get(0).getCityList();
				if (cityList!= null && !cityList.isEmpty()) {
					mCurrentCityName = cityList.get(0).getName();
					List<DistrictModel> districtList = cityList.get(0).getDistrictList();
				}
			}
			//*/
			mProvinceDatas = new String[provinceList.size()];
        	for (int i=0; i< provinceList.size(); i++) {
        		// 遍历所有省的数据
        		mProvinceDatas[i] = provinceList.get(i).getName();
        		List<CityModel> cityList = provinceList.get(i).getCityList();
        		String[] cityNames = new String[cityList.size()];
        		for (int j=0; j< cityList.size(); j++) {
        			// 遍历省下面的所有市的数据
        			cityNames[j] = cityList.get(j).getName();
        			List<DistrictModel> districtList = cityList.get(j).getDistrictList();
        			String[] distrinctNameArray = new String[districtList.size()];
        			DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
        			for (int k=0; k<districtList.size(); k++) {
        				// 遍历市下面所有区/县的数据
        				DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());

        				distrinctArray[k] = districtModel;
        				distrinctNameArray[k] = districtModel.getName();
        			}

        		}
        		// 省-市的数据，保存到mCitisDatasMap
        		mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
        	}
        } catch (Throwable e) {  
            e.printStackTrace();  
        } finally {
        	
        } 
	}
    
    /**
     * 注册监听器
     *
     * @param l 监听器
     */
    public void registerAddrListener(AddressListener l) {
    	listener = l;
    }
    
    /**
     * 移除监听器
     *
     */
    public void removeAddrListener() {
    	listener = null;
    } 
	private void showSelectedResult() {
		//Toast.makeText(mContext, "当前选中:"+mCurrentProviceName+","+mCurrentCityName, Toast.LENGTH_SHORT).show();
	}	
	
	public void setCurrentItemProvince(String province){
		int i=0;
		for(i=0;i<mProvinceDatas.length;i++){
			if(mProvinceDatas[i].trim().equals(province.trim())){
				break;
			}
		}
		mViewProvince.setCurrentItem(i);
	}
}
