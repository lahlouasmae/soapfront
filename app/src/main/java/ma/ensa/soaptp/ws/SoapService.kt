package ma.ensa.soaptp.ws

import android.provider.SyncStateContract.Constants
import ma.ensa.soaptp.beans.Compte
import ma.ensa.soaptp.beans.TypeCompte
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.text.SimpleDateFormat
import java.util.*


class SoapService {

    private val NAMESPACE = "http://ws.tpsoap.projet.ensa.com/"
    private val URL = "http://10.0.2.2:8082/services/ws"
    private val METHOD_GET_COMPTES = "getComptes"
    private val METHOD_CREATE_COMPTE = "createCompte"
    private val METHOD_DELETE_COMPTE = "deleteCompte"
    private val SOAP_ACTION = ""

    @Throws(Exception::class)
    fun getComptes(): List<Compte> {
        val request = SoapObject("http://ws.tpsoap.projet.ensa.com/", "getComptes")
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = true
        envelope.setOutputSoapObject(request)

        val transport = HttpTransportSE(URL)
        transport.debug = true
        transport.call(null, envelope)

        val response = envelope.bodyIn as SoapObject
        val comptes: MutableList<Compte> = mutableListOf()

        for (i in 0 until response.propertyCount) {
            val soapCompte = response.getProperty(i) as SoapObject
            val compte = Compte(
                id = soapCompte.getPropertySafely("id").toString().toLongOrNull(),
                solde = soapCompte.getPropertySafely("solde").toString().toDoubleOrNull() ?: 0.0,
                dateCreation = Date(), // You might want to parse the actual date from the SOAP response
                type = TypeCompte.valueOf(soapCompte.getPropertySafely("type").toString())
            )
            comptes.add(compte)
        }

        return comptes
    }
    fun createCompte(solde: Double, type: TypeCompte): Boolean {
        val request = SoapObject(NAMESPACE, METHOD_CREATE_COMPTE)
        request.addProperty("solde", solde.toString())
        request.addProperty("type", type.name)

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)

        return try {
            val transport = HttpTransportSE(URL)
            transport.debug = true
            transport.call(SOAP_ACTION, envelope)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteCompte(id: Long): Boolean {
        val request = SoapObject(NAMESPACE, METHOD_DELETE_COMPTE)
        request.addProperty("id", id.toString())

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)

        return try {
            val transport = HttpTransportSE(URL)
            transport.debug = true
            transport.call(SOAP_ACTION, envelope)
            envelope.response as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    private fun mapToCompte(soapObject: SoapObject): Compte {
        return Compte(
            id = soapObject.getPropertyAsString("id").toLong(),
            solde = soapObject.getPropertyAsString("solde").toDouble(),
            dateCreation = SimpleDateFormat("yyyy-MM-dd").parse(soapObject.getPropertyAsString("dateCreation")),
            type = TypeCompte.valueOf(soapObject.getPropertyAsString("type"))
        )
    }
}