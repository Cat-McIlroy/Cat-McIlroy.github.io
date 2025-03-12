document.addEventListener("DOMContentLoaded", function() {
    
    //////////////////////////////////////////// CREATE NEW AVAILABILITY LISTING ///////////////////////////////////////////////
    
    // on submission of option (clicking "Add")
    document.getElementById("add-availability").addEventListener("submit", async function(event) {
        
        // use event.preventDefault to prevent page reload on form submission
        event.preventDefault(); 
        
        // clear any previous message
        const messageContainer = document.getElementById("message-container");
        messageContainer.innerHTML = "";
        
        // get the value submitted (drug licenceNo)
        const licenceNo = document.getElementById("shorts").value;
        const isAvailable = true;
        
        // add necessary query parameters
        const url = new URL("http://localhost:8080/pharmacy-drug-availabilities/create");
        url.searchParams.append("licenceNo", licenceNo);
        url.searchParams.append("isAvailable", isAvailable);

        try {
            
            // call backend create pharmacy drug availability API
            const response = await fetch(url, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                credentials: "include"
            });

            messageContainer.style.display = "flex";
            // if the response from the API is ok
            if (response.ok) {
                // add success message to the screen
                messageContainer.innerHTML = "Availability listing added successfully.";
            } 
            
            else {
                // extract the http status from the create availability API and throw it as a new error
                // or if it is an undefined error, display generic error message
                if(response.status == 409){
                    messageContainer.innerHTML = "This account already has an availability listing for the selected product.";
                }
                else{
                    const responseData = await response.json();
                    throw new Error(responseData.error || "An error occurred. Please try again.");
                }
            }
            
        } catch (error) {
            // catch error thrown by try block and display it as an alert
            console.error("Error:", error);
            messageContainer.style.display = "flex";
            messageContainer.innerHTML = error.message;
        }
        
    });
    
});