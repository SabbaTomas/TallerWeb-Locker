const mp = new MercadoPago('TEST-2cd883e8-bf2d-4dd3-8dd5-a6abef77e9c9', { locale: 'es-AR'});

const MP = async () => {
    const idElement = document.getElementById('reservaId');
    const precioElement = document.getElementById('precio');

    if (!idElement || !precioElement) {
        alert("No se encontraron los elementos necesarios en la página.");
        return;
    }

    const id = idElement.innerText;
    const precio = parseFloat(precioElement.innerText);

    let miArticulo;
    try {
        miArticulo = {
            id: id,
            title: "Locker",
            quantity: 1,
            unit_price: precio
        };

        const response = await fetch('api/mp', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(miArticulo)
        });

        const preference = await response.text();
        createCheckoutButton(preference);
    } catch (e) {
        alert("error: " + e)
    }
}

const createCheckoutButton = (preferenceId_OK) => {
    const bricksBuilder = mp.bricks();
    const generateButton = async () => {
        if (window.checkoutButton) window.checkoutButton.unmount()
        bricksBuilder.create("wallet", "wallet_container", {
            initialization: {
                preferenceId: preferenceId_OK,
            },
        });
    }
    generateButton();
}
