package org.example;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class LaptopsBean {

@WebMethod
public int getManufacturerLaptopNumber(String manufacturer) {
        return 0;

}

@WebMethod
    public int getResolutionLaptopNumber(String resolution) {
        return 0;
    }

    @WebMethod
    public Laptop[] getLaptopListByFeatures(String f1, String f2, String f3, String f4, String f5) {
        return null;
    }


}
