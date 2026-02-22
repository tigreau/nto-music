package com.musicshop.data.seeder;

import com.musicshop.model.brand.Brand;
import com.musicshop.model.category.Category;
import com.musicshop.model.product.Product;
import com.musicshop.model.product.ProductCondition;
import com.musicshop.repository.brand.BrandRepository;
import com.musicshop.repository.category.CategoryRepository;
import com.musicshop.repository.product.ProductRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class ProductSeeder implements DataSeeder {

        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;
        private final BrandRepository brandRepository;

        public ProductSeeder(ProductRepository productRepository, CategoryRepository categoryRepository,
                        BrandRepository brandRepository) {
                this.productRepository = productRepository;
                this.categoryRepository = categoryRepository;
                this.brandRepository = brandRepository;
        }

        @Override
        public void seed() {
                if (productRepository.count() >= 150)
                        return;

                // Resolve subcategories (products go on subcategories, not parents)
                Category electricGuitar = categoryRepository.findBySlug("electric-guitar").orElse(null);
                Category acousticGuitar = categoryRepository.findBySlug("acoustic-guitar").orElse(null);
                Category bassGuitar = categoryRepository.findBySlug("bass-guitar").orElse(null);
                Category ukulele = categoryRepository.findBySlug("ukulele").orElse(null);
                Category banjo = categoryRepository.findBySlug("banjo").orElse(null);
                Category mandolin = categoryRepository.findBySlug("mandolin").orElse(null);

                Category drumKit = categoryRepository.findBySlug("drum-kit").orElse(null);
                Category cymbal = categoryRepository.findBySlug("cymbal").orElse(null);
                Category cajon = categoryRepository.findBySlug("cajon").orElse(null);
                Category drumMachine = categoryRepository.findBySlug("drum-machine").orElse(null);
                Category xylophone = categoryRepository.findBySlug("xylophone").orElse(null);

                Category piano = categoryRepository.findBySlug("piano").orElse(null);
                Category organ = categoryRepository.findBySlug("organ").orElse(null);
                Category accordion = categoryRepository.findBySlug("accordion").orElse(null);
                Category synthesizer = categoryRepository.findBySlug("synthesizer").orElse(null);
                Category sampler = categoryRepository.findBySlug("sampler").orElse(null);

                Category saxophone = categoryRepository.findBySlug("saxophone").orElse(null);
                Category trumpet = categoryRepository.findBySlug("trumpet").orElse(null);
                Category flute = categoryRepository.findBySlug("flute").orElse(null);
                Category harmonica = categoryRepository.findBySlug("harmonica").orElse(null);
                Category bagpipe = categoryRepository.findBySlug("bagpipe").orElse(null);
                Category kazoo = categoryRepository.findBySlug("kazoo").orElse(null);

                Category violin = categoryRepository.findBySlug("violin").orElse(null);
                Category viola = categoryRepository.findBySlug("viola").orElse(null);
                Category cello = categoryRepository.findBySlug("cello").orElse(null);
                Category doubleBass = categoryRepository.findBySlug("double-bass").orElse(null);
                Category electricViolin = categoryRepository.findBySlug("electric-violin").orElse(null);

                // Resolve brands
                Brand fender = brandRepository.findBySlug("fender").orElse(null);
                Brand gibson = brandRepository.findBySlug("gibson").orElse(null);
                Brand yamaha = brandRepository.findBySlug("yamaha").orElse(null);
                Brand martin = brandRepository.findBySlug("martin").orElse(null);
                Brand roland = brandRepository.findBySlug("roland").orElse(null);
                Brand ibanez = brandRepository.findBySlug("ibanez").orElse(null);
                Brand taylor = brandRepository.findBySlug("taylor").orElse(null);
                Brand korg = brandRepository.findBySlug("korg").orElse(null);
                Brand nord = brandRepository.findBySlug("nord").orElse(null);
                Brand casio = brandRepository.findBySlug("casio").orElse(null);
                Brand alesis = brandRepository.findBySlug("alesis").orElse(null);
                Brand pearl = brandRepository.findBySlug("pearl").orElse(null);
                Brand ludwig = brandRepository.findBySlug("ludwig").orElse(null);
                Brand hohner = brandRepository.findBySlug("hohner").orElse(null);
                Brand stentor = brandRepository.findBySlug("stentor").orElse(null);
                Brand epiphone = brandRepository.findBySlug("epiphone").orElse(null);
                Brand cordoba = brandRepository.findBySlug("cordoba").orElse(null);
                Brand deering = brandRepository.findBySlug("deering").orElse(null);
                Brand meinl = brandRepository.findBySlug("meinl").orElse(null);
                Brand dw = brandRepository.findBySlug("dw").orElse(null);
                Brand connSelmer = brandRepository.findBySlug("conn-selmer").orElse(null);
                Brand jupiter = brandRepository.findBySlug("jupiter").orElse(null);
                Brand gemeinhardt = brandRepository.findBySlug("gemeinhardt").orElse(null);
                Brand buffetCrampon = brandRepository.findBySlug("buffet-crampon").orElse(null);
                Brand eastman = brandRepository.findBySlug("eastman").orElse(null);
                Brand behringer = brandRepository.findBySlug("behringer").orElse(null);

                // ==================== EXPLICIT PRODUCTS ====================
                if (productRepository.count() < 60) {

                        // --- Guitars & Plucked ---
                        // Electric Guitar (5)
                        createProduct("Fender Player Stratocaster",
                                        "Classic sound with modern feel. Alder body, maple neck.",
                                        new BigDecimal("649.99"), 8, electricGuitar, fender, ProductCondition.EXCELLENT,
                                        true);
                        createProduct("Gibson Les Paul Standard '50s",
                                        "Full PAF-inspired sound. Mahogany body, rosewood fretboard.",
                                        new BigDecimal("2499.99"), 3, electricGuitar, gibson, ProductCondition.EXCELLENT,
                                        true);
                        createProduct("Ibanez RG550",
                                        "High-performance electric. Edge tremolo, fast wizard neck.",
                                        new BigDecimal("899.99"), 5, electricGuitar, ibanez,
                                        ProductCondition.GOOD, false);
                        createProduct("Fender Vintera '60s Telecaster",
                                        "Vintage-voiced single-coils. Classic Tele twang.",
                                        new BigDecimal("999.99"), 6, electricGuitar, fender, ProductCondition.GOOD,
                                        false);
                        createProduct("Epiphone SG Standard",
                                        "Classic SG tone at an accessible price. SlimTaper neck.",
                                        new BigDecimal("449.99"), 10, electricGuitar, epiphone, ProductCondition.EXCELLENT,
                                        false);

                        // Acoustic Guitar (4)
                        createProduct("Martin LX1E Little Martin",
                                        "Portable acoustic-electric. Sitka spruce top, perfect for travel.",
                                        new BigDecimal("449.99"), 10, acousticGuitar, martin, ProductCondition.EXCELLENT,
                                        false);
                        createProduct("Taylor 214ce",
                                        "Grand auditorium acoustic-electric. Sitka spruce and rosewood.",
                                        new BigDecimal("1199.99"), 4, acousticGuitar, taylor,
                                        ProductCondition.EXCELLENT,
                                        true);
                        createProduct("Yamaha FG800",
                                        "Solid spruce top dreadnought. Great for beginners and pros alike.",
                                        new BigDecimal("229.99"), 15, acousticGuitar, yamaha, ProductCondition.EXCELLENT,
                                        false);
                        createProduct("Gibson J-45 Standard",
                                        "The workhorse acoustic. Warm and full tone.",
                                        new BigDecimal("2699.99"), 2, acousticGuitar, gibson, ProductCondition.EXCELLENT,
                                        true);

                        // Bass Guitar (3)
                        createProduct("Fender Player Jazz Bass",
                                        "Versatile bass with two single-coil pickups. Modern C neck.",
                                        new BigDecimal("849.99"), 6, bassGuitar, fender, ProductCondition.EXCELLENT, true);
                        createProduct("Ibanez SR300E",
                                        "Lightweight bass with PowerSpan dual-coil pickups.",
                                        new BigDecimal("399.99"), 8, bassGuitar, ibanez, ProductCondition.EXCELLENT,
                                        false);
                        createProduct("Yamaha TRBX304",
                                        "Active bass with solid mahogany body. Punchy tone.",
                                        new BigDecimal("349.99"), 10, bassGuitar, yamaha, ProductCondition.EXCELLENT, false);

                        // Ukulele (2)
                        createProduct("Cordoba 15CM Concert Ukulele",
                                        "Mahogany concert uke. Warm, rich tone.",
                                        new BigDecimal("69.99"), 20, ukulele, cordoba, ProductCondition.EXCELLENT, false);
                        createProduct("Fender Fullerton Strat Uke",
                                        "Strat-shaped concert ukulele. Built-in pickup.",
                                        new BigDecimal("149.99"), 12, ukulele, fender, ProductCondition.EXCELLENT, true);

                        // Banjo (2)
                        createProduct("Deering Goodtime Two",
                                        "5-string resonator banjo. Bright, loud projection.",
                                        new BigDecimal("799.99"), 4, banjo, deering, ProductCondition.EXCELLENT, true);
                        createProduct("Ibanez B200",
                                        "5-string closed-back banjo. Great for beginners.",
                                        new BigDecimal("299.99"), 7, banjo, ibanez, ProductCondition.EXCELLENT, false);

                        // Mandolin (1)
                        createProduct("Ibanez M510 A-Style Mandolin",
                                        "Classic A-style mandolin. Spruce top, sapele back.",
                                        new BigDecimal("249.99"), 6, mandolin, ibanez, ProductCondition.EXCELLENT, false);

                        // --- Drums & Percussion ---
                        // Drum Kit (5)
                        createProduct("Yamaha DTX402K Electronic Drum Kit",
                                        "Compact e-drum kit. 10 built-in training functions.",
                                        new BigDecimal("449.99"), 6, drumKit, yamaha, ProductCondition.EXCELLENT, false);
                        createProduct("Roland TD-17KVX V-Drums",
                                        "Mesh-head digital drums. Bluetooth audio playback.",
                                        new BigDecimal("1799.99"), 2, drumKit, roland, ProductCondition.GOOD,
                                        true);
                        createProduct("Alesis Nitro Mesh Kit",
                                        "8-piece all-mesh electronic drum kit. Great feel and response.",
                                        new BigDecimal("379.99"), 20, drumKit, alesis, ProductCondition.EXCELLENT, true);
                        createProduct("Pearl Roadshow 5-Piece",
                                        "Complete drum set with hardware and cymbals. Perfect for beginners.",
                                        new BigDecimal("549.99"), 5, drumKit, pearl, ProductCondition.EXCELLENT, false);
                        createProduct("Ludwig Breakbeats by Questlove",
                                        "Compact 4-piece kit. Great for tight spaces and gigging.",
                                        new BigDecimal("499.99"), 3, drumKit, ludwig, ProductCondition.GOOD,
                                        false);

                        // Cymbal (2)
                        createProduct("Meinl HCS Cymbal Set",
                                        "Complete cymbal pack: hi-hats, crash, ride.",
                                        new BigDecimal("199.99"), 10, cymbal, meinl, ProductCondition.EXCELLENT, false);
                        createProduct("Meinl Byzance Traditional Medium Ride 20\"",
                                        "Dark, warm ride cymbal. Hand-hammered B20 bronze.",
                                        new BigDecimal("459.99"), 5, cymbal, meinl, ProductCondition.EXCELLENT, true);

                        // Cajon (2)
                        createProduct("Meinl Headliner Series Cajon",
                                        "Baltic birch body. Dual internal snare wires.",
                                        new BigDecimal("129.99"), 15, cajon, meinl, ProductCondition.EXCELLENT, false);
                        createProduct("Pearl Primero Box Cajon",
                                        "Compact travel cajon. Natural fiberglass front plate.",
                                        new BigDecimal("89.99"), 20, cajon, pearl, ProductCondition.EXCELLENT, false);

                        // Drum Machine (2)
                        createProduct("Roland TR-8S Rhythm Performer",
                                        "Flagship drum machine. ACB modeling of classic Roland rhythm machines.",
                                        new BigDecimal("599.99"), 4, drumMachine, roland, ProductCondition.EXCELLENT, true);
                        createProduct("Alesis SR-16",
                                        "Classic drum machine. 233 realistic sounds.",
                                        new BigDecimal("149.99"), 12, drumMachine, alesis, ProductCondition.EXCELLENT,
                                        false);

                        // Xylophone (1)
                        createProduct("Yamaha YX-230 Xylophone",
                                        "30-note xylophone. Mango wood bars, A=442Hz.",
                                        new BigDecimal("1299.99"), 3, xylophone, yamaha, ProductCondition.EXCELLENT, false);

                        // --- Keys & Synths ---
                        // Piano (5)
                        createProduct("Yamaha P-125 Digital Piano",
                                        "88-key weighted action. Pure CF sound engine.",
                                        new BigDecimal("699.99"), 7, piano, yamaha, ProductCondition.EXCELLENT, true);
                        createProduct("Roland FP-30X",
                                        "88-key portable piano. Bluetooth MIDI, SuperNATURAL sound.",
                                        new BigDecimal("749.99"), 5, piano, roland, ProductCondition.EXCELLENT, false);
                        createProduct("Casio Privia PX-S1100",
                                        "Slim, smart digital piano. Illuminated touch sensor controls.",
                                        new BigDecimal("679.99"), 12, piano, casio, ProductCondition.EXCELLENT, false);
                        createProduct("Nord Piano 5 88",
                                        "Premium stage piano. Virtual hammer and triple sensor keybed.",
                                        new BigDecimal("3999.99"), 2, piano, nord, ProductCondition.EXCELLENT, true);
                        createProduct("Yamaha CLP-745 Clavinova",
                                        "Console digital piano. CFX and Bösendorfer sampling.",
                                        new BigDecimal("2499.99"), 3, piano, yamaha, ProductCondition.EXCELLENT, true);

                        // Synthesizer (3)
                        createProduct("Korg Minilogue XD",
                                        "4-voice analog hybrid synthesizer. Digital multi-engine and effects.",
                                        new BigDecimal("649.99"), 4, synthesizer, korg, ProductCondition.EXCELLENT, true);
                        createProduct("Nord Stage 3 88",
                                        "88-key hammer action stage keyboard. Synth, organ, and piano sections.",
                                        new BigDecimal("4499.99"), 2, synthesizer, nord, ProductCondition.EXCELLENT, true);
                        createProduct("Roland JUNO-DS61",
                                        "61-key lightweight synth. Great for live performance.",
                                        new BigDecimal("699.99"), 6, synthesizer, roland, ProductCondition.EXCELLENT,
                                        false);

                        // Organ (1)
                        createProduct("Nord C2D Combo Organ",
                                        "Dual manual organ. Authentic B3, Vox, and Farfisa tones.",
                                        new BigDecimal("3499.99"), 2, organ, nord, ProductCondition.EXCELLENT, true);

                        // Accordion (2)
                        createProduct("Hohner Panther Diatonic Accordion",
                                        "Compact 31-button accordion. Matte black finish, G/C/F keys.",
                                        new BigDecimal("599.99"), 3, accordion, hohner, ProductCondition.EXCELLENT, true);
                        createProduct("Roland FR-1x V-Accordion",
                                        "Compact digital accordion. Light and small with pro features.",
                                        new BigDecimal("1499.99"), 2, accordion, roland, ProductCondition.EXCELLENT, false);

                        // Sampler (1)
                        createProduct("Roland SP-404MKII",
                                        "Creative sampler and effector. 16 velocity-sensitive pads.",
                                        new BigDecimal("449.99"), 8, sampler, roland, ProductCondition.EXCELLENT, true);

                        // --- Wind & Brass ---
                        // Saxophone (3)
                        createProduct("Yamaha YAS-280 Alto Saxophone",
                                        "Student alto sax. Gold lacquer finish, excellent intonation.",
                                        new BigDecimal("1099.99"), 5, saxophone, yamaha, ProductCondition.EXCELLENT, true);
                        createProduct("Conn-Selmer AS711 Prelude Alto Saxophone",
                                        "Beginner-friendly alto. Rose brass construction.",
                                        new BigDecimal("899.99"), 6, saxophone, connSelmer, ProductCondition.EXCELLENT,
                                        false);
                        createProduct("Jupiter JTS700A Tenor Saxophone",
                                        "Intermediate tenor sax. Warm, centered tone.",
                                        new BigDecimal("1599.99"), 3, saxophone, jupiter, ProductCondition.EXCELLENT, true);

                        // Trumpet (3)
                        createProduct("Yamaha YTR-2330 Trumpet",
                                        "Standard Bb trumpet. Two-piece bell design.",
                                        new BigDecimal("649.99"), 8, trumpet, yamaha, ProductCondition.EXCELLENT, false);
                        createProduct("Jupiter JTR700A Trumpet",
                                        "Intermediate Bb trumpet. Rose brass leadpipe.",
                                        new BigDecimal("799.99"), 5, trumpet, jupiter, ProductCondition.EXCELLENT, true);
                        createProduct("Conn-Selmer 1BR Bb Trumpet",
                                        "Professional-level trumpet. Bright, projecting tone.",
                                        new BigDecimal("1899.99"), 2, trumpet, connSelmer, ProductCondition.EXCELLENT, true);

                        // Flute (2)
                        createProduct("Gemeinhardt 2SP Student Flute",
                                        "Silver-plated student flute. Offset G, closed hole.",
                                        new BigDecimal("549.99"), 10, flute, gemeinhardt, ProductCondition.EXCELLENT, false);
                        createProduct("Yamaha YFL-222 Student Flute",
                                        "Nickel silver body. CY headjoint for easy response.",
                                        new BigDecimal("699.99"), 7, flute, yamaha, ProductCondition.EXCELLENT, true);

                        // Harmonica (2)
                        createProduct("Hohner Special 20 Harmonica",
                                        "Professional harmonica. Durable plastic comb, great response.",
                                        new BigDecimal("49.99"), 100, harmonica, hohner, ProductCondition.EXCELLENT, false);
                        createProduct("Hohner Marine Band 1896",
                                        "Classic diatonic harmonica. Pearwood comb, hand-tuned reeds.",
                                        new BigDecimal("59.99"), 50, harmonica, hohner, ProductCondition.EXCELLENT, true);

                        // Bagpipe (1)
                        createProduct("McCallum Highland Bagpipe Set",
                                        "Full highland bagpipe in African blackwood. Nickel mounts.",
                                        new BigDecimal("1899.99"), 2, bagpipe, null, ProductCondition.EXCELLENT, true);

                        // Kazoo (1)
                        createProduct("Hohner Kazoo Classic",
                                        "Simple yet fun metal kazoo. Silver-plated.",
                                        new BigDecimal("5.99"), 200, kazoo, hohner, ProductCondition.EXCELLENT, false);

                        // --- Bowed Strings ---
                        // Violin (3)
                        createProduct("Stentor Student I Violin Outfit",
                                        "High quality student violin. Hand carved from solid tonewoods.",
                                        new BigDecimal("179.99"), 15, violin, stentor, ProductCondition.EXCELLENT, false);
                        createProduct("Yamaha V3 Series Advanced Student Violin",
                                        "Advanced student violin. Oil varnish shading.",
                                        new BigDecimal("645.00"), 5, violin, yamaha, ProductCondition.EXCELLENT, true);
                        createProduct("Eastman VL200 Violin Outfit",
                                        "Step-up violin. Fully carved spruce and maple.",
                                        new BigDecimal("799.99"), 4, violin, eastman, ProductCondition.EXCELLENT, true);

                        // Viola (2)
                        createProduct("Stentor Student I Viola Outfit",
                                        "Affordable student viola. Solid spruce top.",
                                        new BigDecimal("229.99"), 8, viola, stentor, ProductCondition.EXCELLENT, false);
                        createProduct("Eastman VA80ST Viola",
                                        "Step-up viola. Aged European spruce top.",
                                        new BigDecimal("599.99"), 4, viola, eastman, ProductCondition.EXCELLENT, true);

                        // Cello (2)
                        createProduct("Stentor Student I Cello Outfit",
                                        "Entry-level cello. Hand carved solid spruce top.",
                                        new BigDecimal("499.99"), 5, cello, stentor, ProductCondition.EXCELLENT, false);
                        createProduct("Eastman VC100 Cello Outfit",
                                        "Step-up cello. Rich, warm tone. Solid carved construction.",
                                        new BigDecimal("1199.99"), 3, cello, eastman, ProductCondition.EXCELLENT, true);

                        // Double Bass (1)
                        createProduct("Stentor Student I Double Bass 3/4",
                                        "Student double bass. Laminated spruce top.",
                                        new BigDecimal("999.99"), 3, doubleBass, stentor, ProductCondition.EXCELLENT, false);

                        // Electric Violin (1)
                        createProduct("Yamaha YEV104 Electric Violin",
                                        "4-string electric violin. Stunning skeletal design.",
                                        new BigDecimal("599.99"), 6, electricViolin, yamaha, ProductCondition.EXCELLENT,
                                        true);
                }

                // ==================== RANDOM FILL to 150 ====================
                long currentCount = productRepository.count();
                if (currentCount < 150) {
                        Random random = new Random();

                        // Subcategories grouped by parent for random generation
                        List<Category> allSubcategories = Arrays.asList(
                                        electricGuitar, acousticGuitar, bassGuitar, ukulele, banjo, mandolin,
                                        drumKit, cymbal, cajon, drumMachine, xylophone,
                                        piano, organ, accordion, synthesizer, sampler,
                                        saxophone, trumpet, flute, harmonica, bagpipe, kazoo,
                                        violin, viola, cello, doubleBass, electricViolin);
                        // Remove any nulls
                        List<Category> validSubcats = new ArrayList<>();
                        for (Category c : allSubcategories) {
                                if (c != null)
                                        validSubcats.add(c);
                        }

                        // Brand mappings per subcategory slug
                        Map<String, List<Brand>> subcategoryBrands = new HashMap<>();
                        subcategoryBrands.put("electric-guitar",
                                        Arrays.asList(fender, gibson, ibanez, epiphone, yamaha));
                        subcategoryBrands.put("acoustic-guitar",
                                        Arrays.asList(martin, taylor, yamaha, gibson, ibanez));
                        subcategoryBrands.put("bass-guitar", Arrays.asList(fender, ibanez, yamaha));
                        subcategoryBrands.put("ukulele", Arrays.asList(cordoba, fender, yamaha));
                        subcategoryBrands.put("banjo", Arrays.asList(deering, ibanez));
                        subcategoryBrands.put("mandolin", Arrays.asList(ibanez, yamaha));
                        subcategoryBrands.put("drum-kit",
                                        Arrays.asList(yamaha, roland, alesis, pearl, ludwig, dw));
                        subcategoryBrands.put("cymbal", Arrays.asList(meinl, pearl));
                        subcategoryBrands.put("cajon", Arrays.asList(meinl, pearl));
                        subcategoryBrands.put("drum-machine", Arrays.asList(roland, alesis, korg));
                        subcategoryBrands.put("xylophone", Arrays.asList(yamaha, pearl));
                        subcategoryBrands.put("piano", Arrays.asList(yamaha, roland, casio, nord));
                        subcategoryBrands.put("organ", Arrays.asList(nord, roland, yamaha));
                        subcategoryBrands.put("accordion", Arrays.asList(hohner, roland));
                        subcategoryBrands.put("synthesizer", Arrays.asList(korg, nord, roland, yamaha, behringer));
                        subcategoryBrands.put("sampler", Arrays.asList(roland, korg, alesis));
                        subcategoryBrands.put("saxophone",
                                        Arrays.asList(yamaha, connSelmer, jupiter, buffetCrampon));
                        subcategoryBrands.put("trumpet", Arrays.asList(yamaha, jupiter, connSelmer));
                        subcategoryBrands.put("flute", Arrays.asList(gemeinhardt, yamaha, jupiter));
                        subcategoryBrands.put("harmonica", Arrays.asList(hohner));
                        subcategoryBrands.put("bagpipe", Collections.singletonList(null));
                        subcategoryBrands.put("kazoo", Arrays.asList(hohner));
                        subcategoryBrands.put("violin", Arrays.asList(stentor, yamaha, eastman));
                        subcategoryBrands.put("viola", Arrays.asList(stentor, eastman));
                        subcategoryBrands.put("cello", Arrays.asList(stentor, eastman));
                        subcategoryBrands.put("double-bass", Arrays.asList(stentor, eastman));
                        subcategoryBrands.put("electric-violin", Arrays.asList(yamaha));

                        // Noun templates per subcategory slug
                        Map<String, List<String>> subcategoryNouns = new HashMap<>();
                        subcategoryNouns.put("electric-guitar",
                                        Arrays.asList("Stratocaster", "Telecaster", "SG", "Jazzmaster",
                                                        "Superstrat"));
                        subcategoryNouns.put("acoustic-guitar",
                                        Arrays.asList("Dreadnought", "Concert", "Parlor", "Jumbo",
                                                        "OM"));
                        subcategoryNouns.put("bass-guitar",
                                        Arrays.asList("Jazz Bass", "Precision Bass", "Active Bass",
                                                        "5-String Bass"));
                        subcategoryNouns.put("ukulele",
                                        Arrays.asList("Soprano Uke", "Concert Uke", "Tenor Uke"));
                        subcategoryNouns.put("banjo", Arrays.asList("5-String Banjo", "Tenor Banjo"));
                        subcategoryNouns.put("mandolin", Arrays.asList("A-Style Mandolin", "F-Style Mandolin"));
                        subcategoryNouns.put("drum-kit",
                                        Arrays.asList("Acoustic Kit", "Electronic Kit", "Shell Pack",
                                                        "Compact Kit"));
                        subcategoryNouns.put("cymbal",
                                        Arrays.asList("Crash Cymbal", "Ride Cymbal", "Hi-Hat", "China Cymbal"));
                        subcategoryNouns.put("cajon", Arrays.asList("Snare Cajon", "Travel Cajon"));
                        subcategoryNouns.put("drum-machine",
                                        Arrays.asList("Beat Machine", "Groove Box", "Rhythm Composer"));
                        subcategoryNouns.put("xylophone",
                                        Arrays.asList("Concert Xylophone", "Student Xylophone"));
                        subcategoryNouns.put("piano",
                                        Arrays.asList("Stage Piano", "Digital Piano", "Console Piano",
                                                        "Portable Piano"));
                        subcategoryNouns.put("organ", Arrays.asList("Combo Organ", "Liturgical Organ"));
                        subcategoryNouns.put("accordion",
                                        Arrays.asList("Piano Accordion", "Button Accordion",
                                                        "Digital Accordion"));
                        subcategoryNouns.put("synthesizer",
                                        Arrays.asList("Analog Synth", "FM Synth", "Wavetable Synth",
                                                        "Modular Synth"));
                        subcategoryNouns.put("sampler",
                                        Arrays.asList("Performance Sampler", "Desktop Sampler",
                                                        "Pad Controller"));
                        subcategoryNouns.put("saxophone",
                                        Arrays.asList("Alto Sax", "Tenor Sax", "Soprano Sax",
                                                        "Baritone Sax"));
                        subcategoryNouns.put("trumpet",
                                        Arrays.asList("Bb Trumpet", "Pocket Trumpet", "Flugelhorn"));
                        subcategoryNouns.put("flute",
                                        Arrays.asList("Concert Flute", "Piccolo", "Alto Flute"));
                        subcategoryNouns.put("harmonica",
                                        Arrays.asList("Diatonic Harmonica", "Chromatic Harmonica",
                                                        "Blues Harmonica"));
                        subcategoryNouns.put("bagpipe",
                                        Arrays.asList("Highland Bagpipe", "Practice Chanter"));
                        subcategoryNouns.put("kazoo", Arrays.asList("Metal Kazoo", "Plastic Kazoo"));
                        subcategoryNouns.put("violin",
                                        Arrays.asList("Acoustic Violin", "Student Violin", "Concert Violin"));
                        subcategoryNouns.put("viola",
                                        Arrays.asList("Student Viola", "Intermediate Viola"));
                        subcategoryNouns.put("cello",
                                        Arrays.asList("Student Cello", "Intermediate Cello"));
                        subcategoryNouns.put("double-bass",
                                        Arrays.asList("3/4 Double Bass", "Upright Bass"));
                        subcategoryNouns.put("electric-violin",
                                        Arrays.asList("Silent Violin", "5-String Electric Violin"));

                        String[] adjectives = { "Vintage", "Modern", "Classic", "Premium", "Elite", "Pro", "Custom",
                                        "Special", "Limited", "Studio" };

                        for (int i = 0; i < 150 - currentCount; i++) {
                                Category category = validSubcats.get(random.nextInt(validSubcats.size()));
                                String catSlug = category.getSlug();

                                List<Brand> catBrands = subcategoryBrands.getOrDefault(catSlug,
                                                Collections.emptyList());
                                List<String> catNouns = subcategoryNouns.getOrDefault(catSlug,
                                                Collections.emptyList());

                                if (catNouns.isEmpty())
                                        continue;

                                Brand brand = catBrands.isEmpty() ? null
                                                : catBrands.get(random.nextInt(catBrands.size()));
                                String noun = catNouns.get(random.nextInt(catNouns.size()));
                                String adjective = adjectives[random.nextInt(adjectives.length)];

                                String name = adjective + " " + (brand != null ? brand.getName() : "Generic") + " "
                                                + noun + " " + (1980 + random.nextInt(45));
                                String description = "Automatically generated product description for " + name
                                                + ". Features high quality components and exceptional craftsmanship.";

                                BigDecimal price = new BigDecimal(random.nextInt(4500) + 100 + ".99");
                                int qty = random.nextInt(50) + 1;
                                ProductCondition condition = ProductCondition.values()[random
                                                .nextInt(ProductCondition.values().length)];
                                boolean promoted = random.nextBoolean();

                                createProduct(name, description, price, qty, category, brand, condition, promoted);
                        }
                }
        }

        private Product createProduct(String name, String description, BigDecimal price,
                        int qty, Category category, Brand brand,
                        ProductCondition condition, boolean promoted) {

                // Pre-calculate slug to check for duplicates
                String slug = name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");

                if (productRepository.findBySlug(slug).isPresent()) {
                        System.out.println("Skipping existing product: " + name);
                        return null;
                }

                Product p = new Product();
                p.setName(name);
                p.setDescription(description);
                p.setPrice(price);
                p.setQuantityAvailable(qty);
                p.setCategory(category);
                p.setBrand(brand);
                p.setCondition(condition);
                p.setPromoted(promoted);
                p.setSlug(slug); // Set explicitly

                try {
                        return productRepository.save(p);
                } catch (Exception e) {
                        System.out.println("Failed to seed product " + name + ": " + e.getMessage());
                        return null;
                }
        }
}
