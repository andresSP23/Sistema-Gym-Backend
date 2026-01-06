package com.ansicode.PlantillaSeguridad.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum BusinessErrorCodes {


 NO_CODE(0,HttpStatus.NOT_IMPLEMENTED , "No code"),
  INCORRECT_PASSWORD(300,HttpStatus.BAD_REQUEST , "Incorrect password"),
  NEW_PASSWORD_DOES_NOT_MATCH(301,HttpStatus.BAD_REQUEST , " The new password does not match"),
  ACCOUNT_LOCKED(302,HttpStatus.FORBIDDEN ,"User account is locked"),
  ACCOUNT_DISABLED(303,HttpStatus.FORBIDDEN ,"User account is disabled"),
  BAD_CREDENTIALS(304,HttpStatus.FORBIDDEN ,"Login or password is incorrect");


  @Getter
  private int code;

  @Getter
  private String description;

  @Getter
  private HttpStatus httpStatus;


  BusinessErrorCodes(int code, HttpStatus httpStatus ,String description) {
    this.code = code;
  }
}
