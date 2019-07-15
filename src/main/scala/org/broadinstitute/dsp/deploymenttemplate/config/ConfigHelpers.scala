package org.broadinstitute.dsp.deploymenttemplate.config

import com.typesafe.config.{Config, ConfigFactory, ConfigObject}

trait ConfigHelpers {

  def getConfigObjectOrEmpty(config: Config, path: String): ConfigObject = {
    if (config.hasPath(path))
      config.getObject(path)
    else
      ConfigFactory.empty().root()
  }

}
