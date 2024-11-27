////////////// Written by Catherine ////////////

///////// Christmas Dropdown Menu Function /////////

function dropdownChristmas(){
    const dropdown = document.getElementById('dropdown_click');
    if(dropdown.className=== "topnav christmas_border"){
    dropdown.className+= " mobile";
    }
    else{
    dropdown.className= "topnav christmas_border";
    }
}

const dropdownIcon= document.querySelector(".dropdown_icon");

dropdownIcon.addEventListener("click", function (e){
    dropdownChristmas();
    event.preventDefault();
});

//////// Christmas Snow Background Animation ///////

const snowContainer = document.getElementById("snow-container");
const snowContent = ['&#10052', '&#10053', '&#10054']

const random = (num) => {
  return Math.floor(Math.random() * num);
}

const getRandomStyles = () => {
  const top = random(100);
  const left = random(100);
  const dur = random(10) + 10;
  const size = random(25) + 25;
  return `
    top: -${top}%;
    left: ${left}%;
    font-size: ${size}px;
    animation-duration: ${dur}s;
  `;
}

const createSnow = (num) => {
  for (var i = num; i > 0; i--) {
    var snow = document.createElement("div");
    snow.className = "snow";
    snow.style.cssText = getRandomStyles();
    snow.innerHTML = snowContent[random(3)]
    snowContainer.append(snow);
  }
}

window.addEventListener("load", () => {
  createSnow(30)
});
