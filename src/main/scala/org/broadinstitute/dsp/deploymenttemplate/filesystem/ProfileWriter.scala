package org.broadinstitute.dsp.deploymenttemplate.filesystem

import better.files._

import collection.JavaConverters._
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.broadinstitute.dsp.deploymenttemplate.config.ConfigHelpers
import org.clapper.scalasti.ST

import scala.util.{Failure, Success}

class ProfileWriter extends ConfigHelpers with LazyLogging {

  val profileTemplateDir = File("src/main/resources/templates/service")
  val featuresTemplateDir = File("src/main/resources/templates/features")

  // TODO: allow base dir to be passed in!
  def writeProfile(serviceName: String, config: Config) = {
    // define the service's profile dir
    val profileDir = File(s"output/profiles/$serviceName/terraform/$serviceName")
    // create dir, and all its parents, if it doesn't exist
    profileDir.delete(swallowIOExceptions = true)
    profileDir.createIfNotExists(asDirectory = true, createParents = true)

    // now, define template values to use in replacements
    val apiServicesString = config.getStringList("api-services").asScala.map(s => '"' + s + '"').mkString(",")
    val templateValues = Map(
      "api_services" -> apiServicesString,
      "service_name_env" -> serviceName.toUpperCase,
      "instance_num_hosts" -> config.getNumber("instance_num_hosts"),
      "instance_size" -> config.getString("instance_size")
    )

    // list all files in template dir. for each, render or copy as-is
    profileTemplateDir.list.foreach{ templateFile =>
      logger.info(s"considering service file: $templateFile ...")
      // custom logic per filename
      templateFile.name match {
        case "api-services.tf" | "variables.tf.ctmpl" =>
          // read file contents
          val fileContents = templateFile.contentAsString
          // create ST template, with values from config
          val template = ST(fileContents).addAttributes(templateValues)
          // render template
          val rendered:String = template.render() match {
            case Success(s) => s
            case Failure(ex) =>
              logger.error(s"failed to render template ${templateFile}", ex)
              throw ex
          }
          // write to target dir
          val targetFile = profileDir/templateFile.name
          targetFile.write(rendered)
        case _ =>
          templateFile.copyToDirectory(profileDir)
      }
    }

    // now check individual features we might add to the profile
    featuresTemplateDir.list.foreach{ templateFile =>
      logger.info(s"considering feature file: $templateFile ...")

      val featureConfigKey = templateFile.name.split('.').head
      val featureConfig = getConfigObjectOrEmpty(config, featureConfigKey)
      if (!featureConfig.isEmpty)
        templateFile.copyToDirectory(profileDir)

    }

  }


}
