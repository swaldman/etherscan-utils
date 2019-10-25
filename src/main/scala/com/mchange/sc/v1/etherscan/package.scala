package com.mchange.sc.v1

import play.api.libs.json._
import com.mchange.sc.v1.log.MLevel._

package object etherscan {
  private [etherscan]
  implicit lazy val logger = mlogger("com.mchange.sc.v1.etherscan")

  class Exception( message : String, cause : Throwable = null ) extends java.lang.Exception( message, cause )
  class ErrorResponseException( val status : String, val message : String, val result : JsValue ) extends Exception( s"status: ${status}, message: '${message}', result: '${Json.stringify(result)}'" ) {
    def this( status : String, rawResponse : JsObject ) = {
      this( status, rawResponse.value("message").as[String], rawResponse.value( "result" ) )
    }
  }
}
