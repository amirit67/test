package ir.payebash.Interfaces;

import java.util.List;

import ir.payebash.Models.PayeItem;

/**
 * Created by ZAMAN on 3/17/2018.
 */

public interface IWebservice {
    void getResult(retrofit2.Response<List<PayeItem>> list) throws Exception;

    void getError() throws Exception;
}
