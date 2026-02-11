import { useState } from 'react';
import { Brand, Category, ProductCondition, CONDITION_LABELS, SortOption, SORT_OPTIONS } from '@/types';
import './FilterSidebar.css';

interface FilterSidebarProps {
    brands: Brand[];
    selectedBrands: string[];
    onBrandsChange: (brands: string[]) => void;
    selectedConditions: ProductCondition[];
    onConditionsChange: (conditions: ProductCondition[]) => void;
    minPrice: string;
    maxPrice: string;
    onPriceChange: (min: string, max: string) => void;
    sort: SortOption;
    onSortChange: (sort: SortOption) => void;
    subCategories?: Category[];
    selectedSubcategory?: string;
    onSubcategoryChange?: (slug: string | undefined) => void;
}

const ALL_CONDITIONS: ProductCondition[] = ['NEW', 'EXCELLENT', 'VERY_GOOD', 'GOOD', 'FAIR'];
const VISIBLE_LIMIT = 5;

function CollapsibleList({
    items,
    renderItem,
    label,
}: {
    items: { key: string; label: string }[];
    renderItem: (item: { key: string; label: string }) => React.ReactNode;
    label: string;
}) {
    const [expanded, setExpanded] = useState(false);
    const [search, setSearch] = useState('');

    const needsCollapse = items.length > VISIBLE_LIMIT;
    const filteredItems = search
        ? items.filter(i => i.label.toLowerCase().includes(search.toLowerCase()))
        : items;
    const visibleItems = expanded || !needsCollapse ? filteredItems : filteredItems.slice(0, VISIBLE_LIMIT);

    return (
        <>
            {needsCollapse && expanded && (
                <input
                    type="text"
                    className="filter-search-input"
                    placeholder={`Search ${label.toLowerCase()}...`}
                    value={search}
                    onChange={e => setSearch(e.target.value)}
                />
            )}
            <div className="filter-checkboxes">
                {visibleItems.map(item => renderItem(item))}
            </div>
            {needsCollapse && !expanded && (
                <button className="see-more-btn" onClick={() => setExpanded(true)}>
                    See More ({items.length - VISIBLE_LIMIT} more)
                </button>
            )}
            {needsCollapse && expanded && (
                <button className="see-more-btn" onClick={() => { setExpanded(false); setSearch(''); }}>
                    Show Less
                </button>
            )}
        </>
    );
}

export function FilterSidebar({
    brands,
    selectedBrands,
    onBrandsChange,
    selectedConditions,
    onConditionsChange,
    minPrice,
    maxPrice,
    onPriceChange,
    sort,
    onSortChange,
    subCategories,
    selectedSubcategory,
    onSubcategoryChange,
}: FilterSidebarProps) {
    const toggleBrand = (slug: string) => {
        onBrandsChange(
            selectedBrands.includes(slug)
                ? selectedBrands.filter(b => b !== slug)
                : [...selectedBrands, slug]
        );
    };

    const toggleCondition = (condition: ProductCondition) => {
        onConditionsChange(
            selectedConditions.includes(condition)
                ? selectedConditions.filter(c => c !== condition)
                : [...selectedConditions, condition]
        );
    };

    return (
        <aside className="filter-sidebar">
            {/* Subcategories */}
            {subCategories && subCategories.length > 0 && onSubcategoryChange && (
                <div className="filter-section">
                    <h3 className="filter-title">Subcategory</h3>
                    <CollapsibleList
                        label="Subcategories"
                        items={subCategories.map(sc => ({ key: sc.slug, label: sc.name }))}
                        renderItem={(item) => (
                            <label key={item.key} className="filter-checkbox">
                                <input
                                    type="radio"
                                    name="subcategory"
                                    checked={selectedSubcategory === item.key}
                                    onChange={() =>
                                        onSubcategoryChange(
                                            selectedSubcategory === item.key ? undefined : item.key
                                        )
                                    }
                                />
                                <span>{item.label}</span>
                            </label>
                        )}
                    />
                    {selectedSubcategory && (
                        <button
                            className="clear-filter-btn"
                            onClick={() => onSubcategoryChange(undefined)}
                        >
                            Clear subcategory
                        </button>
                    )}
                </div>
            )}

            {/* Sort */}
            <div className="filter-section">
                <h3 className="filter-title">Sort By</h3>
                <select
                    className="sort-select"
                    value={sort}
                    onChange={e => onSortChange(e.target.value as SortOption)}
                >
                    {SORT_OPTIONS.map(opt => (
                        <option key={opt.value} value={opt.value}>{opt.label}</option>
                    ))}
                </select>
            </div>

            {/* Brand */}
            <div className="filter-section">
                <h3 className="filter-title">Brand</h3>
                <CollapsibleList
                    label="Brands"
                    items={brands.map(b => ({ key: b.slug, label: b.name }))}
                    renderItem={(item) => (
                        <label key={item.key} className="filter-checkbox">
                            <input
                                type="checkbox"
                                checked={selectedBrands.includes(item.key)}
                                onChange={() => toggleBrand(item.key)}
                            />
                            <span>{item.label}</span>
                        </label>
                    )}
                />
            </div>

            {/* Price Range */}
            <div className="filter-section">
                <h3 className="filter-title">Price Range</h3>
                <div className="price-inputs">
                    <input
                        type="number"
                        className="price-input"
                        placeholder="Min"
                        value={minPrice}
                        onChange={e => onPriceChange(e.target.value, maxPrice)}
                    />
                    <span className="price-separator">—</span>
                    <input
                        type="number"
                        className="price-input"
                        placeholder="Max"
                        value={maxPrice}
                        onChange={e => onPriceChange(minPrice, e.target.value)}
                    />
                </div>
            </div>

            {/* Condition */}
            <div className="filter-section">
                <h3 className="filter-title">Condition</h3>
                <div className="filter-checkboxes">
                    {ALL_CONDITIONS.map(condition => (
                        <label key={condition} className="filter-checkbox">
                            <input
                                type="checkbox"
                                checked={selectedConditions.includes(condition)}
                                onChange={() => toggleCondition(condition)}
                            />
                            <span>{CONDITION_LABELS[condition]}</span>
                        </label>
                    ))}
                </div>
            </div>
        </aside>
    );
}
