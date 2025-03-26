document.addEventListener("DOMContentLoaded", async function() {
    
    try {
        
        // call backend API to get user's pharmacy name
        const response = await fetch('http://localhost:8080/pharmacies/check-auth', {
            method: 'GET',
            credentials: 'include',
        });

        const welcomeMessageDiv = document.getElementById("welcome");

        if (response.ok) {
            
            const data = await response.json();
            
            // extract account username from API response
            const pharmacyName = data.pharmacyName;
            console.log("Updated pharmacy name:", data.pharmacyName);
            const welcomeText = document.createElement("strong");
            welcomeText.id = "welcome-message";
            welcomeText.textContent = "Welcome, " + data.pharmacyName;
            welcomeMessageDiv.appendChild(welcomeText);
        }
        else {
        // handle error
        console.error("An error occurred. Could not retrieve account username.");
        }
    } 
        
    catch (error) {
        // handle any errors thrown by the fetch request
        console.error("Error:", error);
    }
    
});