package com.ansicode.SistemaAdministracionGym.handler;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class BussinessException  extends RuntimeException{


    private final BusinessErrorCodes errorCode;

    public BussinessException(BusinessErrorCodes errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

}
