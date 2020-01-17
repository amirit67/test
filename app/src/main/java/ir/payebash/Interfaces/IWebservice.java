package ir.payebash.Interfaces;

import java.util.List;

import ir.payebash.Models.BaseResponse;
import ir.payebash.Models.CityItem;
import ir.payebash.Models.TkModel;
import ir.payebash.Models.event.EventModel;
import ir.payebash.Models.event.detail.EventDetailsModel;
import ir.payebash.Models.event.story.StoryModel;
import ir.payebash.Models.user.LoginModel;

/**
 * Created by ZAMAN on 3/17/2018.
 */

public interface IWebservice {
    void getResult(retrofit2.Response<List<EventModel>> list) throws Exception;

    void getError() throws Exception;

    interface ITkModel {
        void getResult(TkModel s) throws Exception;

        void getError(boolean error) throws Exception;
    }

    interface ILogin {
        void getResult(LoginModel s) throws Exception;

        void getError(String error) throws Exception;
    }

    interface IForgotPassword {
        void getResult(BaseResponse s) throws Exception;

        void getError(String error) throws Exception;
    }

    interface OnLoadMoreListener {

        void onLoadMore();
    }

    interface setListenerCity {

        void onItemCheck(CityItem item);
    }

    interface TitleMain {
        public void FragName(String name);
    }

    interface IBottomSheetNavigation {
        public void showBottomSheet();
    }

    interface HideActionbar {
        void IsHide(boolean ishide);
    }

    interface IEventDetails {
        void getResult(EventDetailsModel event) throws Exception;

        void getError(String error) throws Exception;
    }

    interface IStoriesEvents {
        void getResult(List<StoryModel> stories) throws Exception;

        void getError(String error) throws Exception;
    }
}
