const accessType = document.getElementById('accessType');
const priceGroup = document.getElementById('priceGroup');
const price = document.getElementById('price');

function syncPriceField() {
    const paid = accessType.value === 'PAID';

    priceGroup.hidden = !paid;
    price.required = paid;

    if (!paid) {
        price.value = '';
    }
}

accessType.addEventListener('change', syncPriceField);
syncPriceField();
