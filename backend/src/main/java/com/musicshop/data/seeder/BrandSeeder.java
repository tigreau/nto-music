package com.musicshop.data.seeder;

import com.musicshop.model.brand.Brand;
import com.musicshop.repository.brand.BrandRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BrandSeeder implements DataSeeder {

    private final BrandRepository brandRepository;

    public BrandSeeder(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    @Transactional
    public void seed() {
        String[][] brands = {
                // Guitars & Plucked
                { "Fender", "fender" }, { "Gibson", "gibson" }, { "Yamaha", "yamaha" },
                { "Martin", "martin" }, { "Ibanez", "ibanez" }, { "Taylor", "taylor" },
                { "Epiphone", "epiphone" }, { "Cordoba", "cordoba" }, { "Deering", "deering" },
                // Drums & Percussion
                { "Pearl", "pearl" }, { "Ludwig", "ludwig" }, { "Roland", "roland" },
                { "Alesis", "alesis" }, { "Meinl", "meinl" }, { "DW", "dw" },
                // Keys & Synths
                { "Korg", "korg" }, { "Nord", "nord" }, { "Casio", "casio" },
                { "Hohner", "hohner" },
                // Wind & Brass
                { "Conn-Selmer", "conn-selmer" }, { "Jupiter", "jupiter" },
                { "Gemeinhardt", "gemeinhardt" }, { "Buffet Crampon", "buffet-crampon" },
                // Bowed Strings
                { "Stentor", "stentor" }, { "Eastman", "eastman" },
                // General
                { "Behringer", "behringer" }
        };

        for (String[] b : brands) {
            if (brandRepository.findBySlug(b[1]).isEmpty()) {
                Brand brand = new Brand();
                brand.setName(b[0]);
                brand.setSlug(b[1]);
                brandRepository.save(brand);
            }
        }
    }
}
