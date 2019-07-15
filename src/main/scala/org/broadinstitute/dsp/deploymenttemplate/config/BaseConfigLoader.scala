package org.broadinstitute.dsp.deploymenttemplate.config

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}

class BaseConfigLoader {

  // TODO: make this a singleton so we only load it up once
  def getBaseConfig: Config = {
    // read the conf files for this execution
    // TODO: these should be accepted as arguments, not hardcoded
    val reference = ConfigFactory.parseFile(new File("src/main/resources/reference.conf"))
    val application = ConfigFactory.parseFile(new File("src/main/resources/application.conf"))
    val allConf = application
      .withFallback(reference)

    allConf

  }

}
