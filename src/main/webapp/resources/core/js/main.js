$(document).ready(function () {
    console.log("Document is ready!");

    // Inicializar tooltips de Bootstrap
    if ($('[data-bs-toggle="tooltip"]').length) {
        console.log("Initializing tooltips");
        $('[data-bs-toggle="tooltip"]').tooltip();
    } else {
        console.log("No tooltips found");
    }

    // Manejar cambios en los inputs de radio
    if ($('input[name="reservaSeleccionada"]').length) {
        console.log("Binding change event to reservaSeleccionada inputs");
        $('input[name="reservaSeleccionada"]').on('change', function () {
            let montoTotal = $(this).closest('li').find('.monto').text();
            console.log("Monto total:", montoTotal);
            $('#montoTotal').val(montoTotal);
        });
    } else {
        console.log("No reservaSeleccionada inputs found");
    }
});
