package com.davidruffner.inventorytrackercontroller.db.repositories;

import com.davidruffner.inventorytrackercontroller.db.entities.ScannableItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ScannableItemRepository  extends CrudRepository<ScannableItem, String> {
    @Query(value = """
            select si
            from ScannableItem si
            where si.mainBarcode = :mainBarcode
        """)
    Optional<ScannableItem> getScannableItemByMainBarcode(String mainBarcode);

    @Query(value = """
            select si
            from ScannableItem si
            where si.altBarcode = :altBarcode
        """)
    Optional<ScannableItem> getScannableItemByAltBarcode(String altBarcode);
}
