package com.mlg.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import org.bson.types.ObjectId


/**
 * Custom Kotlinx serializer for MongoDB's [ObjectId].
 *
 * Serializes an [ObjectId] as a JSON object with a `"$oid"` field, matching MongoDB extended JSON format:
 * ```json
 * { "$oid": "507f1f77bcf86cd799439011" }
 * ```
 *
 * This format is often used in MongoDB tools and drivers for interoperability.
 */
object MongoObjectIdSerializer : KSerializer<ObjectId> {

    /**
     * Descriptor for the ObjectId structure, representing a single string element named `$oid`.
     */
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ObjectId") {
        element<String>("\$oid")
    }

    /**
     * Serializes the [ObjectId] into the MongoDB extended JSON format.
     *
     * @param encoder The [Encoder] used to encode the value.
     * @param value The [ObjectId] to serialize.
     */
    override fun serialize(encoder: Encoder, value: ObjectId) {
        val composite = encoder.beginStructure(descriptor)
        composite.encodeStringElement(descriptor, 0, value.toHexString())
        composite.endStructure(descriptor)
    }

    /**
     * Deserializes the JSON format back into an [ObjectId].
     *
     * @param decoder The [Decoder] used to decode the value.
     * @return A reconstructed [ObjectId] from the `$oid` field.
     * @throws SerializationException If the `$oid` field is missing or malformed.
     */
    override fun deserialize(decoder: Decoder): ObjectId {
        val composite = decoder.beginStructure(descriptor)
        var oid: String? = null
        loop@ while (true) {
            when (val index = composite.decodeElementIndex(descriptor)) {
                0 -> oid = composite.decodeStringElement(descriptor, 0)
                CompositeDecoder.DECODE_DONE -> break@loop
                else -> throw SerializationException("Unexpected index: $index")
            }
        }
        composite.endStructure(descriptor)
        return ObjectId(oid ?: throw SerializationException("Missing \$oid field"))
    }
}