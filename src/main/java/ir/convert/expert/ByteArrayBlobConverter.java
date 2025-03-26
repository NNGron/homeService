package ir.convert.expert;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;

@Converter
public class ByteArrayBlobConverter implements AttributeConverter<byte[], Blob> {

    @Override
    public Blob convertToDatabaseColumn(byte[] bytes) {
        if (bytes == null){
            return null;
        }
        try {
            return new SerialBlob(bytes);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to convert byte array to Blob", e);
        }
    }

    @Override
    public byte[] convertToEntityAttribute(Blob blob) {
        if (blob == null){
            return null;
        }
        try {
            return blob.getBytes(1, (int) blob.length());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to convert Blob to byte array", e);
        }
    }
}
