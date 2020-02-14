package ir.payebash.Interfaces;

import java.util.List;

import ir.payebash.models.BaseResponse;
import ir.payebash.models.CityItem;
import ir.payebash.models.FilterFeedItem;
import ir.payebash.models.RequestItem;
import ir.payebash.models.TkModel;
import ir.payebash.models.contacts.ContactItem;
import ir.payebash.models.contacts.FollowItem;
import ir.payebash.models.event.EventModel;
import ir.payebash.models.event.detail.EventDetailsModel;
import ir.payebash.models.event.story.StoryModel;
import ir.payebash.models.login.LoginModel;
import ir.payebash.models.parsijoo.ParsijooItem;
import ir.payebash.models.user.UserInfoModel;

/**
 * Created by ZAMAN on 3/17/2018.
 */

public interface IWebservice {
    void getResult(List<EventModel> list) throws Exception;

    void getError(String s) throws Exception;

    interface IAddress {
        void getResult(ParsijooItem s) throws Exception;

        void getError(String error) throws Exception;
    }

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

    interface IRequestJoin {
        void getResult(List<RequestItem> requestItem) throws Exception;

        void getError(String error) throws Exception;
    }

    interface IUserInfo {
        void getResult(UserInfoModel userInfoModel) throws Exception;

        void getError(String error) throws Exception;
    }

    interface ICheckContacts {
        void getResult(List<ContactItem> contactItems) throws Exception;

        void getError(String error) throws Exception;
    }

    interface IFollow {
        void getResult(FollowItem contactItems) throws Exception;

        void getError(String error) throws Exception;
    }


    interface OnLoadMoreListener {
        void onLoadMore();
    }

    interface setListenerCity {
        void onItemCheck(CityItem item);
    }

    interface IsetListenerSubject {
        void onItemCheck(FilterFeedItem item);
    }

    interface IsetListenerCity {
        void onItemCheck(CityItem item);
    }

    interface IFilterListenerCity {
        void onSearch(String s);
    }

    interface TitleMain {
        void FragName(String name);
    }

    interface IBottomSheetNavigation {
        void showBottomSheetNavigation();

        void showBottomSheetRating(UserInfoModel result);
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
