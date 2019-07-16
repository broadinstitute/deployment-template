package org.broadinstitute.dsp.deploymenttemplate.filesystem

import better.files._

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.antlr.runtime.Token
import org.broadinstitute.dsp.deploymenttemplate.config.ConfigHelpers
import org.clapper.scalasti.ST
import org.stringtemplate.v4.compiler.STLexer

import scala.util.{Failure, Success}

class ProfileWriter extends ConfigHelpers with LazyLogging {


  val featuresTemplateDir = File("src/main/resources/templates/features")

  // TODO: allow base dir to be passed in!
  def writeProfile(serviceName: String, config: Config) = {
    // define the service's profile dir
    val profileDir = File(s"output/profiles/$serviceName/terraform/$serviceName")
    // create dir, and all its parents, if it doesn't exist
    profileDir.delete(swallowIOExceptions = true)
    profileDir.createIfNotExists(asDirectory = true, createParents = true)

    // define profile template dir, based on service type
    val serviceType = config.getString("service_type")
    val profileTemplateDir = File(s"src/main/resources/templates/service/${serviceType.toLowerCase}")

    // list all files in template dir. for each, render or copy as-is
    profileTemplateDir.list.foreach{ templateFile =>
      logger.info(s"considering service file: $templateFile ...")
      // custom logic per filename
      templateFile.name match {
        case "api-services.tf" | "variables.tf.ctmpl" =>
          // read file contents
          val fileContents = templateFile.contentAsString
          // create ST template, with values from config
          val st = ST(fileContents)

          // find all replaceable attributes in this template
          val tokenStream = st.nativeTemplate.impl.tokens
          val allTokens:List[Token] = (0 to tokenStream.range).map { i =>
            tokenStream.get(i)
          }.toList
          val attrNames:Set[String] = allTokens.filter(_.getType == STLexer.ID).map(_.getText).toSet

          // find all config values for these attributes
          val templateValues = attrNames.map { attr => attr -> config.getString(attr)}.toMap
          logger.info(s"using $templateValues")

          // render template
          val template = st.addAttributes(templateValues)
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
