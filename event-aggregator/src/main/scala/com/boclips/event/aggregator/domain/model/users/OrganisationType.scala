package com.boclips.event.aggregator.domain.model.users

sealed class OrganisationType(val name: String) extends Serializable {

  override def toString: String = getClass.getSimpleName
}

object API_ORGANISATION extends OrganisationType("API")

object SCHOOL_ORGANISATION extends OrganisationType("SCHOOL")

object DISTRICT_ORGANISATION extends OrganisationType("DISTRICT")

object LTI_DEPLOYMENT extends OrganisationType("LTI_DEPLOYMENT")

object OrganisationType {
  def from(value: String): OrganisationType = value match {
    case "API" => API_ORGANISATION
    case "SCHOOL" => SCHOOL_ORGANISATION
    case "DISTRICT" => DISTRICT_ORGANISATION
    case "LTI_DEPLOYMENT" => LTI_DEPLOYMENT
    case _ => throw new IllegalArgumentException(value)
  }
}
