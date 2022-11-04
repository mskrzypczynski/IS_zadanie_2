package org.example;

import javax.jws.WebService;

@WebService(endpointInterface = "LaptopsWebServiceInterface")
public class LaptopsBean implements LaptopsInterface {
    @Override
    public int getManufacturerLaptopNumber(String manufacturer) {
        return 0;
    }

    @Override
    public int getResolutionLaptopNumber(String resolution) {
        return 0;
    }

    @Override
    public Laptop[] getLaptopListByFeatures(String f1, String f2, String f3, String f4, String f5) {
        return null;
    }


}
