/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils.ontology

import android.content.Context
import com.hp.hpl.jena.ontology.Individual
import com.hp.hpl.jena.ontology.OntClass
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.kent.university.privelt.database.PriVELTDatabase
import com.kent.university.privelt.model.CurrentUser
import com.kent.university.privelt.model.Service
import com.kent.university.privelt.model.UserData
import java.io.OutputStreamWriter

class OntologyBuilder(private val priVELTDatabase: PriVELTDatabase) {

    fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")

    private var model: OntModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)
    private val priveltURI = "https://privelt.ac.uk/"

    //Classes
    private var personClass = createClass("Person")
    private var serviceClass = createClass("Service")
    private var dataClass = createClass("Data")
    private var onlineAccountClass = createClass("OnlineAccount")
    private var dataPackageClass = createClass("DataPackage")
    private var organisationClass = createClass("Organisation")

    init {
        createProperty(dataClass, personClass, "ownedBy")
        createProperty(dataClass, dataPackageClass, "construct")
        createProperty(dataPackageClass, serviceClass, "requiredby")
        createProperty(onlineAccountClass, personClass, "account")
        createProperty(onlineAccountClass, dataPackageClass, "attach")
        createProperty(serviceClass, onlineAccountClass, "create")
        createProperty(serviceClass, organisationClass, "providedby")
    }

    fun build(context: Context) {


        //instantiate DAOs
        val currentUserDao = priVELTDatabase.currentUserDao()
        val serviceDao = priVELTDatabase.serviceDao()
        val userDataDao = priVELTDatabase.userDataDao()

        //get current user and services
        val currentUser = currentUserDao?.currentUserSync
        val services = serviceDao?.allServices

        //create person node
        val person = createPersonNode(currentUser!!)

        for (service in services!!) {

            //build service node
            val serviceOnt = createService(service)

            //build online account node
            val onlineAccountOnt = createOnlineAccountNode(service)

            val userData = userDataDao?.getUserDataForAService(service.id)

            //find corresponding organisation
            for (organisation in Organisation.values()) {
                if (organisation.ownedServices.contains(service.name)) {
                    //found organisation for service
                    val organisationOnt = createOrganisationNode(organisation.label)
                }
            }

            for (data in userData!!) {

                //build user data node
                val dataOnt = createDataNode(data)

                //build data package node
                val dataPackageOnt = createDataPackageNode(data.type)

            }
        }
        save(context)
    }

    private fun createService(service: Service): Individual {
        val serviceOnt = serviceClass.createIndividual(priveltURI + service.name)
        serviceOnt.addLabel(service.name, "en")
        return serviceOnt
    }

    private fun createDataNode(data: UserData): Individual {
        val className = data.title.capitalizeWords().replace(" ", "")
        val subClass = model.createClass(priveltURI + className)
        subClass.addLabel(className, "en")
        dataClass.addSubClass(subClass)

        val dataOnt = subClass.createIndividual(priveltURI + data.value)
        dataOnt.addLabel(data.value, "en")
        return dataOnt
    }

    private fun createPersonNode(current: CurrentUser): Individual {
        val person = personClass.createIndividual(priveltURI + current.firstName)
        person.addLabel(current.firstName, "en")
        return person
    }

    private fun createDataPackageNode(type: String) {
        val className = type.capitalizeWords().replace(" ", "")
        val subClass = model.createClass(priveltURI + className)
        subClass.addLabel(className, "en")
        dataPackageClass.addSubClass(subClass)

        //val dataPackageOnt = dataPackageClass.createIndividual(priveltURI + type)
        //dataPackageOnt.addLabel(type, "en")
        //return dataPackageOnt
    }

    private fun createOnlineAccountNode(service: Service): Individual {
        val dataOnlineAccountOnt = onlineAccountClass.createIndividual(priveltURI + service.user)
        dataOnlineAccountOnt.addLabel(service.user, "en")
        return dataOnlineAccountOnt
    }

    private fun createOrganisationNode(organisation: String): Individual {
        val organisationOnt = organisationClass.createIndividual(priveltURI + organisation)
        organisationOnt.addLabel(organisation, "en")
        return organisationOnt
    }

    private fun createProperty(from: OntClass, to:OntClass, relationShip: String) {
        val requiredBy = model.createObjectProperty(priveltURI + relationShip)
        requiredBy.addDomain(from)
        requiredBy.addRange(to)
        requiredBy.addLabel(relationShip, "en")
    }

    private fun createClass(className: String): OntClass {
        val ontClass = model.createClass(priveltURI + className)
        ontClass.addLabel(className, "en")
        return ontClass;
    }

    private fun save(context: Context) {
        val file = context.openFileOutput("ontology_debug.owl", Context.MODE_PRIVATE)
        val outputStreamWriter = OutputStreamWriter(file)
        model.write(outputStreamWriter)
        outputStreamWriter.close()
    }
}
