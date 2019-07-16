package org.broadinstitute.dsp.deploymenttemplate.config

import com.typesafe.config.ConfigFactory

import collection.JavaConverters._
import com.typesafe.scalalogging.LazyLogging

object ServiceConfig extends ConfigHelpers with LazyLogging {

  def getServiceConfig(serviceName: String) = {
    val baseConfig = new BaseConfigLoader().getBaseConfig

    // find the global defaults that apply to all services
    val serviceDefault = getConfigObjectOrEmpty(baseConfig, "service-defaults")
    if (serviceDefault.isEmpty)
      logger.warn("service-defaults config is empty; is this intentional?")

    // see if we have a config specific to this service
    val serviceSpecific = getConfigObjectOrEmpty(baseConfig, s"services.$serviceName")

    // layer service-specific on top of service-defaults
    val service = serviceSpecific
      .withFallback(serviceDefault)

    // finally, munge a few special cases
    val apiServicesString = service.toConfig.getStringList("api-services").asScala.map(s => '"' + s + '"').mkString(",")
    val serviceNameEnv = serviceName.toUpperCase

    val extras = ConfigFactory.parseMap(Map(
      "service_name_env" -> serviceNameEnv,
      "api_services" -> apiServicesString
    ).asJava)

    service.withFallback(extras)
  }

}
