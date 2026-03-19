/**
 * Parse products.ts from frontend and generate SQL seed script
 * for PetFit backend PostgreSQL database.
 */
const fs = require('fs');
const path = require('path');

const productsFile = path.resolve(__dirname, '../../PetFit-Front/src/data/products.ts');
const reviewsFile = path.resolve(__dirname, '../../PetFit-Front/src/data/mockReviews.ts');
const outputFile = path.resolve(__dirname, '../src/main/resources/data-seed.sql');

// Read products.ts
const productsContent = fs.readFileSync(productsFile, 'utf-8');

// Extract product objects using regex
const productRegex = /\{\s*id:\s*(\d+),\s*name:\s*'([^']*)',\s*price:\s*(\d+),\s*imageUrl:\s*'([^']*)',\s*description:\s*[\s\S]*?category:\s*'([^']*)',\s*discountRate:\s*(\d+),\s*rating:\s*([\d.]+),\s*reviewCount:\s*(\d+),\s*color:\s*\[([^\]]*)\],\s*size:\s*\[([^\]]*)\],(?:\s*productUrl:\s*[\s\S]*?)?\s*date:\s*'([^']*)'/g;

const products = [];
let match;
while ((match = productRegex.exec(productsContent)) !== null) {
  const colors = match[9].replace(/'/g, '').split(',').map(c => c.trim()).filter(Boolean);
  const sizes = match[10].replace(/'/g, '').split(',').map(s => s.trim()).filter(Boolean);

  // Extract productUrl if exists
  const block = productsContent.substring(match.index, match.index + 2000);
  const urlMatch = block.match(/productUrl:\s*'([^']*)'/);

  products.push({
    id: parseInt(match[1]),
    name: match[2],
    price: parseInt(match[3]),
    imageUrl: match[4],
    category: match[5],
    discountRate: parseInt(match[6]),
    rating: parseFloat(match[7]),
    reviewCount: parseInt(match[8]),
    colors,
    sizes,
    productUrl: urlMatch ? urlMatch[1] : null,
    date: match[11],
  });
}

console.log(`Parsed ${products.length} products`);

// Category mapping
const categoryMap = {
  'outer': 'Outer',
  'top': 'Top',
  'one-piece': 'One-piece',
  'muffler': 'Muffler',
  'shoes': 'Shoes',
  'accessory': 'Accessory',
  'etc': 'Etc',
};

const categories = Object.entries(categoryMap);

// Build SQL
let sql = `-- PetFit Seed Data
-- Generated from frontend products.ts
-- Run this after application starts and tables are created

-- Categories
`;

categories.forEach(([key, name], i) => {
  sql += `INSERT INTO categories (name, display_order, created_at, updated_at) VALUES ('${name}', ${i + 1}, NOW(), NOW()) ON CONFLICT DO NOTHING;\n`;
});

sql += `\n-- Products\n`;

// Determine isNew, isHot, isSale for each product
const sortedByDate = [...products].sort((a, b) => b.date.localeCompare(a.date));
const newProductIds = new Set(sortedByDate.slice(0, 24).map(p => p.id));
const sortedByReview = [...products].sort((a, b) => b.reviewCount - a.reviewCount);
const hotProductIds = new Set(sortedByReview.slice(0, 24).map(p => p.id));

for (const p of products) {
  const escapedName = p.name.replace(/'/g, "''");
  const isNew = newProductIds.has(p.id);
  const isHot = hotProductIds.has(p.id);
  const isSale = p.discountRate > 0;
  const escapedUrl = p.productUrl ? `'${p.productUrl.replace(/'/g, "''")}'` : 'NULL';

  // Find category id (1-indexed)
  const catIndex = categories.findIndex(([key]) => key === p.category);
  const categoryId = catIndex >= 0 ? catIndex + 1 : 'NULL';

  sql += `INSERT INTO products (name, description, price, stock_quantity, category_id, thumbnail_url, is_new, is_hot, is_sale, discount_rate, product_url, created_at, updated_at) VALUES ('${escapedName}', '', ${p.price}, 100, ${categoryId}, '${p.imageUrl}', ${isNew}, ${isHot}, ${isSale}, ${p.discountRate}, ${escapedUrl}, '${p.date}T00:00:00', NOW());\n`;
}

// Product Options (size x color combinations)
sql += `\n-- Product Options\n`;
for (const p of products) {
  for (const size of p.sizes) {
    for (const color of p.colors) {
      const escapedColor = color.replace(/'/g, "''");
      sql += `INSERT INTO product_options (product_id, size, color, additional_price, stock_quantity, created_at, updated_at) VALUES (${p.id}, '${size}', '${escapedColor}', 0, 50, NOW(), NOW());\n`;
    }
  }
}

// Reviews
const reviewsContent = fs.readFileSync(reviewsFile, 'utf-8');
const reviewRegex = /\{\s*id:\s*(\d+),\s*productId:\s*(\d+),\s*userName:\s*'([^']*)',\s*rating:\s*(\d+),\s*date:\s*'([^']*)',\s*content:\s*[\s\S]*?'([^']*)'/g;

sql += `\n-- Reviews\n`;
let reviewMatch;
let reviewCount = 0;
while ((reviewMatch = reviewRegex.exec(reviewsContent)) !== null) {
  const escapedContent = reviewMatch[6].replace(/'/g, "''");
  const escapedUser = reviewMatch[3].replace(/'/g, "''");
  const date = reviewMatch[5].replace(/\./g, '-');
  sql += `INSERT INTO reviews (user_id, product_id, rating, content, created_at, updated_at) VALUES ('${escapedUser}', ${reviewMatch[2]}, ${reviewMatch[4]}, '${escapedContent}', '${date}T00:00:00', NOW());\n`;
  reviewCount++;
}

console.log(`Generated ${reviewCount} reviews`);

fs.writeFileSync(outputFile, sql, 'utf-8');
console.log(`SQL seed file written to: ${outputFile}`);
console.log(`Total: ${products.length} products, ${reviewCount} reviews`);
