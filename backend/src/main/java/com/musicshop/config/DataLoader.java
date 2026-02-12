package com.musicshop.config;

import com.musicshop.data.seeder.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

        private final UserSeeder userSeeder;
        private final BrandSeeder brandSeeder;
        private final CategorySeeder categorySeeder;
        private final ProductSeeder productSeeder;
        private final ReviewSeeder reviewSeeder;

        public DataLoader(UserSeeder userSeeder,
                        BrandSeeder brandSeeder,
                        CategorySeeder categorySeeder,
                        ProductSeeder productSeeder,
                        ReviewSeeder reviewSeeder) {
                this.userSeeder = userSeeder;
                this.brandSeeder = brandSeeder;
                this.categorySeeder = categorySeeder;
                this.productSeeder = productSeeder;
                this.reviewSeeder = reviewSeeder;
        }

        @Override
        public void run(String... args) throws Exception {
                userSeeder.seed();
                brandSeeder.seed();
                categorySeeder.seed();
                productSeeder.seed();
                reviewSeeder.seed();
        }
}