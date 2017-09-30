window.onload = () => {
    let active = location.pathname.split("/")[0] || "home";
    $(`#${active}`).addClass("active");
};