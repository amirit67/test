package ir.payebash.Interfaces;

import java.util.List;

import ir.payebash.Models.CityItem;
import ir.payebash.Models.PayeItem;

/**
 * Created by ZAMAN on 3/17/2018.
 */

public interface IWebservice {
    void getResult(retrofit2.Response<List<PayeItem>> list) throws Exception;

    void getError() throws Exception;


    interface OnLoadMoreListener {

        void onLoadMore();
    }

    interface setListenerCity {

        void onItemCheck(CityItem item);
    }

    interface TitleMain {
        public void FragName(String name);
    }

    interface HideActionbar {
        void IsHide(boolean ishide);
    }
}
