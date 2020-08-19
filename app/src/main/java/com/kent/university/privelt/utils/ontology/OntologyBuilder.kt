/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils.ontology

import android.content.Context
import com.hp.hpl.jena.ontology.*
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.kent.university.privelt.database.PriVELTDatabase
import com.kent.university.privelt.model.CurrentUser
import com.kent.university.privelt.model.Service
import com.kent.university.privelt.model.UserData
import java.io.OutputStreamWriter

class OntologyBuilder(private val priVELTDatabase: PriVELTDatabase) {

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }

    private var model: OntModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)
    private val priveltURI = "https://privelt.ac.uk/"

    //Classes
    private var personClass = createClass("Person")
    private var serviceClass = createClass("Service")
    private var dataClass = createClass("Data")
    private var onlineAccountClass = createClass("OnlineAccount")
    private var dataPackageClass = createClass("DataPackage")
    private var organisationClass = createClass("Organisation")

    private var construct = createProperty(dataClass, dataPackageClass, "construct")
    private var ownedBy = createProperty(dataClass, personClass, "ownedBy")
    private var requiredBy = createProperty(dataPackageClass, serviceClass, "requiredBy")
    private var account = createProperty(onlineAccountClass, personClass, "account")
    private var attach = createProperty(onlineAccountClass, dataPackageClass, "attach")
    private var create = createProperty(serviceClass, onlineAccountClass, "create")
    private var providedBy = createProperty(serviceClass, organisationClass, "providedBy")

    companion object {
        var packageId = 0
        var dataId = 0
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
            onlineAccountOnt.addProperty(account, person)
            serviceOnt.addProperty(create, onlineAccountOnt)

            val userData = userDataDao?.getUserDataForAService(service.id)

            //find corresponding organisation
            for (organisation in Organisation.values()) {
                if (organisation.ownedServices.contains(service.name)) {
                    //found organisation for service
                    val organisationOnt = createOrganisationNode(organisation.label)
                    serviceOnt.addProperty(providedBy, organisationOnt)
                }
            }

            val dataSorted = userData?.groupBy { it.date }?.map { it.value }

            for (map in dataSorted!!) {

                //build data package node
                val dataPackageOnt = createDataPackageNode()
                dataPackageOnt.addProperty(requiredBy, serviceOnt)
                onlineAccountOnt.addProperty(attach, dataPackageOnt)

                for (data in map) {

                    //build user data node
                    val dataOnt = createDataNode(data)
                    dataOnt.addProperty(construct, dataPackageOnt)
                    dataOnt.addProperty(ownedBy, person)
                }
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

        val dataOnt = subClass.createIndividual(priveltURI + data.value + "_" + dataId)
        dataOnt.addLabel(data.value + "_" + dataId++, "en")
        return dataOnt
    }

    private fun createPersonNode(current: CurrentUser): Individual {
        val person = personClass.createIndividual(priveltURI + current.firstName)
        person.addLabel(current.firstName, "en")
        return person
    }

    private fun createDataPackageNode() : Individual {
        val dataPackageOnt = dataPackageClass.createIndividual(priveltURI + "dp_" + packageId)
        dataPackageOnt.addLabel("dp_" + packageId++, "en")
        return dataPackageOnt
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

    private fun createProperty(from: OntClass, to:OntClass, relationShip: String): ObjectProperty? {
        val requiredBy = model.createObjectProperty(priveltURI + relationShip)
        requiredBy.addDomain(from)
        requiredBy.addRange(to)
        requiredBy.addLabel(relationShip, "en")
        return requiredBy
    }

    private fun createClass(className: String): OntClass {
        val ontClass = model.createClass(priveltURI + className)
        ontClass.addLabel(className, "en")
        return ontClass
    }

    private fun save(context: Context) {
        val file = context.openFileOutput("ontology_debug.owl", Context.MODE_PRIVATE)
        val outputStreamWriter = OutputStreamWriter(file)
        model.write(outputStreamWriter)
        outputStreamWriter.close()
    }
}
