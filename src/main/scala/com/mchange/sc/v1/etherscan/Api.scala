package com.mchange.sc.v1.etherscan

import java.net.URL
import scala.concurrent.{blocking,Future,ExecutionContext}
import com.mchange.sc.v2.lang.borrow
import com.mchange.sc.v1.consuela.ethereum._
import com.mchange.sc.v1.log.MLevel._
import play.api.libs.json._

object Api {
  object Simple {
    def apply( apiKey : String ) : Simple = new Simple( apiKey )
  }
  final class Simple( apiKey : String ) extends Api {
    def getVerifiedAbi( address : EthAddress )( implicit ec : ExecutionContext ) : Future[jsonrpc.Abi] = Future {
      val url = new URL( s"https://api.etherscan.io/api?module=contract&action=getabi&address=0x${address.hex}&apikey=${apiKey}" )
      val rawResponse = blocking( borrow( url.openStream() )( Json.parse ) ).as[JsObject]
      FINER.log( s"Etherscan raw getabi response: ${rawResponse}" )
      val status = rawResponse.value("status").as[String]
      val rawAbi = {
        status match {
          case "1" => rawResponse.value("result")
          case _ => throw new ErrorResponseException( status, rawResponse )
        }
      }
      Json.parse(rawAbi.as[String]).as[ jsonrpc.Abi ]
    }
  }
}
trait Api {
  def getVerifiedAbi( address : EthAddress )( implicit ec : ExecutionContext ) : Future[jsonrpc.Abi]
}
