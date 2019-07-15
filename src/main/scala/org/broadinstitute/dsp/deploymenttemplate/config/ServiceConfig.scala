package org.broadinstitute.dsp.deploymenttemplate.config

import com.typesafe.scalalogging.LazyLogging


class ServiceConfig extends ConfigHelpers with LazyLogging {

  def getServiceConfig(serviceName: String) = {
    val baseConfig = new BaseConfigLoader().getBaseConfig

    // find the global defaults that apply to all services
    val serviceDefault = getConfigObjectOrEmpty(baseConfig, "service-defaults")
    if (serviceDefault.isEmpty)
      logger.warn("service-defaults config is empty; is this intentional?")

    // see if we have a config specific to this service
    val serviceSpecific = getConfigObjectOrEmpty(baseConfig, s"services.$serviceName")

    // final service config is the layering of service-specific on top of service-defaults
    val service = serviceSpecific
      .withFallback(serviceDefault)

    service
  }


}
