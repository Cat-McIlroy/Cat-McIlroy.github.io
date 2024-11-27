const productData = new Map();
productData.set('Balloons', "Add a burst of color to your special occasion with our high-quality balloons! Perfect for birthdays, anniversaries, or anything else you need a balloon for. Inflated with helium, our balloons are sure to 'elevate' any event!");
productData.set('Champagne', "Why not treat yourself or a loved one to something special? Our champagne offers a bit of celebration in every bubble. Cheers to you and whatever you are celebrating.");
productData.set('Chocolates', "Indulge yourself in our fantastic chocolate pralines. Our pralines offer a perfect blend of luxury and sweetness in every bite.");
productData.set('Teddy Bear', "Our Teddy is an irresistibly soft cuddle companion. The perfect long distance companion for when you can't be with your loved one.");
productData.set('Vase', "Want to keep your new flowers fresh for as long as you can? Why not buy one of our vases to give your flowers the best home possible?");


document.getElementById('balloons-picture').addEventListener('click', (event) => updateModal('Balloons'));
document.getElementById('champagne-picture').addEventListener('click', (event) => updateModal('Champagne'));
document.getElementById('chocolates-picture').addEventListener('click', (event) => updateModal('Chocolates'));
document.getElementById('teddy-picture').addEventListener('click', (event) => updateModal('Teddy Bear'));
document.getElementById('vase-picture').addEventListener('click', (event) => updateModal('Vase'));

function updateModal(productName) {
    document.getElementById('product-title').innerHTML = productName;
    document.getElementById('product-description').innerHTML = productData.get(productName);
}

document.getElementById('order-button').addEventListener('click', (event) => alert("This is where we would integrate the checkout flow of our chosen payment provider."))