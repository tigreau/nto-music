/**
 * Maps category names to their corresponding image paths.
 * These images are stored in the public/images/categories directory.
 */
export const getCategoryImage = (categoryName?: string): string | null => {
    if (!categoryName) return null;

    const normalized = categoryName.toLowerCase().trim();

    // Map of normalized category names to image file names
    // Note: ensure these files exist in public/images/categories/
    const categoryMap: Record<string, string> = {
        // Specific subcategories (prioritize these)
        'electric guitar': 'guitars.png',
        'acoustic guitar': 'acoustic_guitar.png',
        'bass guitar': 'bass_guitar.png',
        'ukulele': 'ukulele.png',
        'banjo': 'banjo.png',
        'mandolin': 'mandolin.png',

        'drum kit': 'drums.png',
        'cymbal': 'cymbal.png',
        'cajon': 'cajon.png',
        'drum machine': 'drum_machine.png',
        'xylophone': 'xylophone.png',

        'piano': 'piano.png',
        'organ': 'organ.png',
        'accordion': 'accordion.png',
        'synthesizer': 'keyboards.png',
        'sampler': 'sampler.png',

        'saxophone': 'saxophone.png',
        'trumpet': 'trumpet.png',

        'flute': 'flute.png',
        'harmonica': 'harmonica.png',
        'bagpipe': 'bagpipe.png',
        'kazoo': 'kazoo.png',

        'violin': 'traditional.png',
        'viola': 'viola.png',
        'cello': 'cello.png',
        'double bass': 'double_bass.png',
        'electric violin': 'electric_violin.png',

        // Broad / Parent categories (fallbacks)
        'guitars': 'guitars.png',
        'guitar': 'guitars.png',
        'plucked': 'guitars.png',

        'drums': 'drums.png',
        'percussion': 'drums.png',

        'keys': 'keyboards.png',
        'keyboards': 'keyboards.png',
        'synths': 'keyboards.png',

        'wind': 'saxophone.png',
        'brass': 'trumpet.png',

        'traditional': 'traditional.png',
        'strings': 'traditional.png',

        'accessories': 'accessories.png',
        'software': 'software.png',
        'mixers': 'mixers.png',
        'microphones': 'microphones.png',
        'headphones': 'headphones.png'
    };

    // Direct match
    if (categoryMap[normalized]) {
        return `/images/categories/${categoryMap[normalized]}`;
    }

    // Partial match (e.g. "Electric Guitars" -> "electric guitar")
    // Sort keys by length descending to ensure "acoustic guitar" is matched before "guitar"
    const keys = Object.keys(categoryMap).sort((a, b) => b.length - a.length);
    const key = keys.find(k => normalized.includes(k));

    if (key) {
        return `/images/categories/${categoryMap[key]}`;
    }

    return null;
};
