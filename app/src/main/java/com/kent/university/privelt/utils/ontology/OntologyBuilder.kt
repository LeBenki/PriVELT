/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils.ontology

import android.content.Context
import com.hp.hpl.jena.ontology.OntClass
import com.hp.hpl.jena.ontology.OntDocumentManager.NS
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.kent.university.privelt.database.PriVELTDatabase
import com.kent.university.privelt.model.CurrentUser
import com.kent.university.privelt.model.Service
import com.kent.university.privelt.model.UserData
import java.io.OutputStreamWriter


class OntologyBuilder(private val priVELTDatabase: PriVELTDatabase) {

    companion object {
        var linkCount = 0
    }

    private var model: OntModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)

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
                    createLink(serviceOnt, organisationOnt, "provided by")
                }
            }

            for (data in userData!!) {

                //build user data node
                val dataOnt = createDataNode(data)

                //build data package node
                val dataPackageOnt = createDataPackageNode(data.type)

                createLink(person, dataOnt, "owned by")

                createLink(dataOnt, dataPackageOnt, "construct")

                createLink(dataPackageOnt, serviceOnt, "required by")

                createLink(onlineAccountOnt, person, "account")
                createLink(onlineAccountOnt, dataOnt, "attach")
                createLink(serviceOnt, onlineAccountOnt, "create")
            }
        }
        save(context)
    }

    private fun createService(service: Service): OntClass {
        val serviceOnt = model.createClass(NS + service.name + "_" + service.id)
        serviceOnt.addLabel(service.name, "en")
        return serviceOnt
    }

    private fun createDataNode(data: UserData): OntClass {
        val dataOnt = model.createClass(NS + data.title + "_" + data.id)
        dataOnt.addLabel(data.title, "en")
        return dataOnt
    }

    private fun createPersonNode(current: CurrentUser): OntClass {
        val person = model.createClass(NS + "Person")
        person.addLabel(current.firstName, "en")
        return person
    }

    private fun createDataPackageNode(type: String): OntClass {
        val dataPackageOnt = model.createClass(NS + type)
        dataPackageOnt.addLabel(type, "en")
        return dataPackageOnt
    }

    private fun createOnlineAccountNode(service: Service): OntClass {
        val dataOnlineAccountOnt = model.createClass(NS + service.user + "_" + service.id)
        dataOnlineAccountOnt.addLabel(service.user, "en")
        return dataOnlineAccountOnt
    }

    private fun createOrganisationNode(organisation: String): OntClass {
        val organisationOnt = model.createClass(NS + organisation)
        organisationOnt.addLabel(organisation, "en")
        return organisationOnt
    }

    private fun createLink(from: OntClass, to:OntClass, relationShip: String) {
        val requiredBy = model.createObjectProperty(NS + relationShip + "_" + linkCount++)
        requiredBy.addDomain(from)
        requiredBy.addRange(to)
        requiredBy.addLabel(relationShip, "en")
    }

    private fun save(context: Context) {
        val file = context.openFileOutput("ontology_debug.rdf", Context.MODE_PRIVATE)
        val outputStreamWriter = OutputStreamWriter(file)
        model.write(outputStreamWriter)
        outputStreamWriter.close()
    }
}
