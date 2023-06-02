package com.step.sacannership.api;

import com.step.sacannership.model.bean.BindResultBean;
import com.step.sacannership.model.bean.DeliveryBaseBean;
import com.step.sacannership.model.bean.DeliveryBean;
import com.step.sacannership.model.bean.DeliveryBillPalletDetailsBean;
import com.step.sacannership.model.bean.DeliveryMaterial;
import com.step.sacannership.model.bean.LogisticsBean;
import com.step.sacannership.model.bean.LogisticsListBean;
import com.step.sacannership.model.bean.MaterialBean;
import com.step.sacannership.model.bean.NpBindSaveBean;
import com.step.sacannership.model.bean.ProductItem;
import com.step.sacannership.model.bean.Request;
import com.step.sacannership.model.bean.Result;
import com.step.sacannership.model.bean.ScanDeliveryMaterialBean;
import com.step.sacannership.model.bean.TrayInfoBean;
import com.step.sacannership.model.bean.UnBind;
import com.step.sacannership.model.bean.UpdateInfo;
import com.step.sacannership.model.bean.UserBean;
import com.step.sacannership.update.UpdateBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;

public interface ApiService {
    /**
     * 检查更新
     * */
    @GET("api/apps/latest/{applicationID}")
    Flowable<UpdateBean> getVersionInfo(@Path("applicationID") String applicationID);
    /**
     * 下载apk
     * */
    @Streaming
    @GET("{filePath}")
    Flowable<ResponseBody> downloadApk(@Path("filePath")String path);
    /**登录*/
    @POST("account/login")
    Flowable<UserBean> login(@Body Request body);
    /**扫发货单查看*/
    @GET("sapDeliveryBills/{billNO}")
    Flowable<DeliveryBean> getDeliveryList(@Path("billNO") String billNO);

    /**扫托盘查看*/
    @GET("deliveryBillPallets/{palletNO}")
    Flowable<TrayInfoBean> trayInfo(@Path("palletNO") String billNO);
    /**
     * 托盘绑定
     * */
    @POST("deliveryBillPallets/bind")
    Flowable<ResponseBody> bindTray(@Body TrayInfoBean infoBean);
    /**
     * 解除绑定
     * */
    @POST("deliveryBillPallets/unbind")
    Flowable<ResponseBody> unBind(@Body UnBind unBind);
    /**
     * 发运
     * 托盘发运(新：增加物流信息记录)
     * */
    @GET("deliveryBillPallets/delivery/{palletNO}")
    Flowable<ResponseBody> delivery(@Path("palletNO") String palletNO);

    /**
     * 发运-无托盘
     * 无托盘发货-发货单发运(新：增加物流信息记录)
     * */
    @POST("sapDeliveryBills/npDelivery")
    Flowable<ResponseBody> deliveryUnPalletNew(@Body Request logisticsBean);
    /**
     * 托盘检验
     * */
    @GET("deliveryBillPallets/qualified/{palletNO}")
    Flowable<ResponseBody> testPallet(@Path("palletNO")String palletNO );
    /**
     * 撤销托盘检验
     * */
    @GET("deliveryBillPallets/qualifiedRevoke/{palletNO}")
    Flowable<ResponseBody> cancelTestPallet(@Path("palletNO")String palletNO );
    /**
     * 获取全部物流列表
     * */
    @GET("logisticsCompanies/all")
    Flowable<List<LogisticsListBean>> getLogisticsList();
    /**
     * 发货单检验
     * */
    @GET("sapDeliveryBills/qualified/{billNO}")
    Flowable<ResponseBody> testDelivery(@Path("billNO")String billNO);
    /**
     * 无托盘发货-发货单检验
     * */
    @GET("sapDeliveryBills/npQualified/{billNO}")
    Flowable<ResponseBody> testDeliveryUnPallet(@Path("billNO")String billNO);
    /**
     * 扫发货单查看物料
     * deliveryBillMaterialBarcodes/{palletId}/{billNO}
     * 查看绑定的物料
     * */
    @GET("deliveryBillMaterialBarcodes/{palletId}/{billNO}/{materialNO}/{rowIndex}")
    Flowable<List<ScanDeliveryMaterialBean>> scanMaterial(@Path("palletId") int palletId , @Path("billNO")String billNO, @Path("materialNO")String nmaterialNO, @Path("rowIndex") String rowIndex);
    /**
     * 扫发货单查看物料
     *
     * 查看绑定的物料
     * 无托盘
     * */
    @GET("deliveryBillMaterialBarcodes/np/{billNO}/{materialNO}/{rowIndex}")
    Flowable<List<ScanDeliveryMaterialBean>> scanMaterialNopallet(@Path("billNO")String billNO, @Path("materialNO")String nmaterialNO, @Path("rowIndex") String rowIndex);
    /**
     * 发货单物料绑定
     * */
    @POST("deliveryBillMaterialBarcodes/bind")
    Flowable<Map<String, Object>> bindMaterial(@Body ScanDeliveryMaterialBean material);
    /**
     * 无托盘发货-发货单条码绑定
     * */
    @POST("deliveryBillMaterialBarcodes/npBind")
    Flowable<Map<String, Object>> bindMaterialUnPallet(@Body ScanDeliveryMaterialBean material);
    /**
     * 发货单物料解除绑定
     * */
    @POST("deliveryBillMaterialBarcodes/unbind")
    Flowable<ResponseBody> unBindMaterial(@Body ScanDeliveryMaterialBean material);
    /**
     * 扫发货单查看-出库：只显示发货单物料数量
     * */
    @GET("sapDeliveryBills/original/{billNO}")
    Flowable<DeliveryBean> getMaterialLists(@Path("billNO") String billNO);
    /**
     * 无托盘发货-扫发货单查看-(出库、绑定、检验、发运)
     * */
    @GET("sapDeliveryBills/npBill/{billNO}")
    Flowable<DeliveryBean> getMaterialListsNoPallet(@Path("billNO") String billNO);
    /**
     * 无托盘发货-保存数量
     * */
    @POST("deliveryBillNoPallets/npBind")
    Flowable<ResponseBody> saveNpNum(@Body NpBindSaveBean saveBean);
    /**无托盘发货单解绑*/
    @GET("/deliveryBillNoPallets/unbind/{deliveryBillId}")
    Flowable<ResponseBody> unBindDelivery(@Path("deliveryBillId") int billId);
    /**
     * 修改发货单物料数量（出库）
     */
    @PUT("sapDeliveryBills")
    Flowable<ResponseBody> sapDeliverySubmit(@Body DeliveryBaseBean deliveryBean);

    /**
     * 出库撤销
     * */
    @GET("sapDeliveryBills/outboundRevoke/{billNO}")
    Flowable<ResponseBody> deliverySubmitCancel(@Path("billNO")String billNO);
    /**
     * 发运撤销
     * */
    @GET("deliveryBillPallets/deliveryRevoke/{palletNO}")
    Flowable<ResponseBody> deliveryPalletCancel(@Path("palletNO") String palletNO);
    /**
     * 发运撤销-无托盘
     * */
    @GET("sapDeliveryBills/deliveryRevoke/{billNO}")
    Flowable<ResponseBody> deliveryPalletCancelUnPallet(@Path("billNO") String palletNO);
    /**
     * /
     * 同步一个条码
     * */
    @GET("ppComponentBindings/syncOne/{barCode}")
    Flowable<ResponseBody> syncBarcode(@Path("barCode") String barCode);
    @GET("snSerialNos/save")
    Flowable<ResponseBody> saveSerialNo(@Query("sn") String sn, @Query("materialNo") String materialNo);
    /**
     * 获取一个发货单的托盘号
     * */
    @POST("deliveryBillPallets/getPalletNO/{billNO}")
    Flowable<List<String>> getPallets(@Path("billNO")String billNO);
    /**
     * 获取托盘到货信息
     * */
    @GET("deliveryBillPallets/arriving/{palletNO}")
    Flowable<TrayInfoBean> getPalletArrive(@Path("palletNO") String billNO);
    /**
     * 到货保存
     * */
    @POST("deliveryBillPallets/arriving/{palletNO}")
    Flowable<ResponseBody> savePalletArrive(@Path("palletNO") String billNO);

    @POST("/deliveryBillMaterialBarcodes/binding")
    Flowable<BindResultBean> bindingMaterial(@Body Map<String, Object> map);

    @POST("/deliveryBillMaterialBarcodes/npBinding")
    Flowable<BindResultBean> bindingMaterialNoPallet(@Body Map<String, Object> map);
    @GET("deliveryBillPallets/snNum/{palletNO}")
    Flowable<Integer> getSNNum(@Path("palletNO") String palletNo);

    @GET("deliveryBillMaterialBarcodes/unbindSn/{sn}")
    Flowable<ResponseBody> backUnbind(@Path("sn") String sn);
    @GET("productionOrderPartsSns/page")
    Flowable<Result<ProductItem>> getProductList(@QueryMap Request request);


}
