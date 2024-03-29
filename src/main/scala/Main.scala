import org.broadinstitute.dsp.deploymenttemplate.config.{ServiceConfig, ServiceList}
import org.broadinstitute.dsp.deploymenttemplate.filesystem.ProfileWriter

object Main extends App {

  val serviceList = new ServiceList().getServiceList

  serviceList.foreach{ serviceKey =>
    println(s"$serviceKey...")

    val service = new ServiceConfig().getServiceConfig(serviceKey)

    println(service)

    new ProfileWriter().writeProfile(serviceKey, service.toConfig)

  }


  // make target dir
  val targetDir = new java.io.File("output/profiles")






  // TODO: move away/recreate target render dir
  // TODO: read in configs - reference.conf default, application.conf override
    // TODO: service-defaults as service defaults
    // TODO: loop through defined services, apply override to default
  // TODO: for each defined service, write out separate TF file



  // TODO: Scalasti for templating, to avoid existing ${...} declarations? http://software.clapper.org/scalasti/#simple-examples

  // TODO: Apache FileUtils for file management



}