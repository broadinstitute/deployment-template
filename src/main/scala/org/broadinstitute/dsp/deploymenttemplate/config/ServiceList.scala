package org.broadinstitute.dsp.deploymenttemplate.config

import collection.JavaConverters._
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success, Try}


class ServiceList extends ConfigHelpers with LazyLogging {

  // list of services to deploy is: all services defined in "services" key, unless there is a
  // "service-whitelist" value, in which case use that.

  def getServiceList: List[String] = {
    val baseConfig = new BaseConfigLoader().getBaseConfig

    // try to load a "service-whitelist" array from config
    val whitelist:Option[List[String]] = Try(baseConfig.getStringList("service-whitelist")) match {
      case Success(serviceList) => Some(serviceList.asScala.toList)
      case Failure(ex) =>
        logger.info("service-whitelist key not present.")
        None
    }

    // check all services defined in "services", based on their config keys
    val serviceKeys:List[String] = getConfigObjectOrEmpty(baseConfig,  "services").keySet().asScala.toList

    // warn if whitelist exists and elides defined services
    whitelist.foreach { wl =>
      val elided = serviceKeys.diff(wl)
      if (elided.nonEmpty)
        logger.info(s"service-whitelist config elides some services: $elided")
    }

    // use whitelist if present; else use iterated keys
    val serviceList = whitelist.getOrElse(serviceKeys)

    serviceList
  }

}
