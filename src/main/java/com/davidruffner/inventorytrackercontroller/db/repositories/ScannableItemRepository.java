package com.davidruffner.inventorytrackercontroller.db.repositories;

import com.davidruffner.inventorytrackercontroller.db.entities.ScannableItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ScannableItemRepository  extends CrudRepository<ScannableItem, String> {
    @Query(value = """
            select ScannableItem 
            from ScannableItem si
            where si.mainBarcode = :mainBarcode
        """)
    public ScannableItem getScannableItemByMainBarcode(String mainBarcode);
}
