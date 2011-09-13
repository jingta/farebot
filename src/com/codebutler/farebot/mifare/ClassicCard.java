/**
 * 
 */
package com.codebutler.farebot.mifare;

import java.math.BigInteger;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.nfc.Tag;
import android.nfc.tech.MifareClassic;


/**
 * @author jingta
 *
 */
public class ClassicCard extends MifareCard {
	
	protected ClassicCard(byte[] tagId, Date scannedAt) {
		super(tagId, scannedAt);
	}
	
	public static ClassicCard  dumpTag (byte[] tagId, Tag tag) throws Exception {

		MifareClassic tech = MifareClassic.get(tag);
        tech.connect();
        
        
        try {
        	// Dump card data
        	Logger l = Logger.getLogger("MifareClassicCardInfo");
        	l.log(Level.INFO, "MifareCard");
        	l.log(Level.INFO, "TAG ID:");
        	for (byte b : tech.getTag().getId()) {
        		l.log(Level.INFO, Byte.toString(b));
        	}
        	l.log(Level.INFO, "Blocks: " + tech.getBlockCount());
        	l.log(Level.INFO, "Sectors: " + tech.getSectorCount());
        	l.log(Level.INFO, "Size: " + tech.getSize());
        	l.log(Level.INFO, "Type: " + tech.getType());
        	String type = "unknown";
        	if (tech.getType() == MifareClassic.TYPE_CLASSIC)
        		type = "classic";
        	else if (tech.getType() == MifareClassic.TYPE_PLUS)
        		type = "plus";
        	else if (tech.getType() == MifareClassic.TYPE_PRO)
        		type = "pro";
        	l.log(Level.INFO, "TypeString: " + type);
        	l.log(Level.INFO, "Connected: " + tech.isConnected());
        	
           int blockCount = 0;
           int blockIndex = 0;
           for(int j = 0; j < tech.getSectorCount(); j++){
        	   // authenticate the sector
        	   if(tech.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT) || 
        			   tech.authenticateSectorWithKeyA(j, MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY)){//MifareClassic.KEY_NFC_FORUM)){//MifareClassic.KEY_DEFAULT)){
        		   blockCount = tech.getBlockCountInSector(j);
        		   for(int i = 0; i < blockCount; i++){
        			   //blockIndex = tech.sectorToBlock(j);
        			   byte[] data = tech.readBlock(i);    
        			   // 7) Convert the data into a string from Hex format.
        			   l.info(toHex(data));
        			   //blockIndex++;
        		   }
        	   }else{ // Authentication failed - Handle it
        		   l.info("Auth failed on sector " + j);
        		   //TODO: get keys
        		   
        	   }
           }
        	
        	for (int i = 0; i < tech.getBlockCount(); i ++){
        		l.log(Level.INFO, "Block " + i + ": " + tech.readBlock(i));
        	}
        } finally {
            if (tech.isConnected())
                tech.close();
        }

        return new ClassicCard(tagId, new Date());
	}
	
	public static String toHex(byte[] bytes) {
	    BigInteger bi = new BigInteger(1, bytes);
	    return String.format("%0" + (bytes.length << 1) + "x", bi);
	}
	

	/* (non-Javadoc)
	 * @see com.codebutler.farebot.mifare.MifareCard#getCardType()
	 */
	@Override
	public CardType getCardType() {
		return CardType.MifareClassic;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
