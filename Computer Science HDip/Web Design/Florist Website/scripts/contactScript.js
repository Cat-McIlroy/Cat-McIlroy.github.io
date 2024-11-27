const submitButton = document.getElementById('submit-button');
const email = document.getElementById('email');
const phoneNumber = document.getElementById('phone-number');
const contactReason = document.getElementById('contact-reason');
const message = document.getElementById('message');

var submitButtonHasBeenPressed = false;

//source: https://stackoverflow.com/questions/46155/how-can-i-validate-an-email-address-in-javascript
const validateEmail = (email) => {
    if (String(email).toLowerCase().match(/^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|.(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/)) {
        return true;
    } else {
        return false;
    }
}

const validatePhoneNumber = (number) => {
    if (String(number).toLowerCase().match(/^08[35679]\d{7}$|^0[1245679]\d{6,9}$/)) {
        return true;
    } else {
        return false;
    }
}

const validateContactReason = (reason) => {
    if (reason != '') {
        return true;
    } else {
        return false;
    }
}

const validateMessage = (message) => {
    if (message != '') {
        return true;
    } else {
        return false;
    }
}

function highlightInvalidInputs() {
    submitButtonHasBeenPressed = true;
    if (validateEmail(email.value)) {
        document.getElementById('email').classList.remove('invalid-input');
    } else {
        document.getElementById('email').classList.add('invalid-input');
        document.getElementById('email').placeholder = '';
    }

    if (validatePhoneNumber(phoneNumber.value)) {
        document.getElementById('phone-number').classList.remove('invalid-input');
    } else {
        document.getElementById('phone-number').classList.add('invalid-input');
        document.getElementById('phone-number').placeholder = '';
    }

    if (validateContactReason(contactReason.value)) {
        document.getElementById('contact-reason').classList.remove('invalid-input');
    } else {
        document.getElementById('contact-reason').classList.add('invalid-input');
    }

    if (validateMessage(message.value)) {
        document.getElementById('message').classList.remove('invalid-input');
    } else {
        document.getElementById('message').classList.add('invalid-input');
    }

}

function checkButtonActivation() {
    let emailIsValid = validateEmail(email.value);
    let phoneIsValid = validatePhoneNumber(phoneNumber.value);
    let contactReasonIsValid = validateContactReason(contactReason.value);
    let messageIsValid = validateMessage(message.value);


    if (emailIsValid && phoneIsValid && contactReasonIsValid && messageIsValid) {
        document.getElementById('submit-button').classList.remove('form-button-disabled');
        document.getElementById('submit-button').classList.add('form-button');
    } else {
        document.getElementById('submit-button').classList.remove('form-button');
        document.getElementById('submit-button').classList.add('form-button-disabled');
    }

    if (submitButtonHasBeenPressed) {
        highlightInvalidInputs();
    }
}

function submitForm() {
    if (document.getElementById('submit-button').classList.contains('form-button-disabled')) {
        console.log('Button is disabled');
        return;
    }
    document.getElementById('contact-form').style.display = 'none';
    document.getElementById('contact-page-intro').style.display = 'none';
    document.getElementById('thank-you-message').style.display = 'inline';
}

function downloadFile() {
    const link = document.createElement('a');
    const text = "Email: " + email.value + "\r\nPhone Number: " + phoneNumber.value + "\r\nContact Reason: " + contactReason.value + "\r\nMessage: " + message.value;
    const file = new Blob([text], { type: 'text/plain;' });
    link.href = URL.createObjectURL(file);
    link.download = 'YourData.txt';
    link.click();
    URL.revokeObjectURL(link.href);
}

email.addEventListener('change', (event) => checkButtonActivation());
phoneNumber.addEventListener('change', (event) => checkButtonActivation());
contactReason.addEventListener('change', (event) => checkButtonActivation());
message.addEventListener('change', (event) => checkButtonActivation());
submitButton.addEventListener('click', (event) => highlightInvalidInputs());
submitButton.addEventListener('click', (event) => submitForm());
document.getElementById('download-button').addEventListener('click', (event) => downloadFile());
