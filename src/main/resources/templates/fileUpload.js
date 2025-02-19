// /src/main/resources/static/fileUpload.js
let uploadXHR = null; 

async function uploadFile() {
    const fileInput = document.getElementById('fileInput');
    const file = fileInput.files[0];
    const uploadButton = document.querySelector('button');
    const progressContainer = document.getElementById('progressContainer');
    const progressBar = document.getElementById('progressBar');
    const progressText = document.getElementById('progressText');
    const statusMessage = document.getElementById('statusMessage');
    
    if (!file) {
        alert('Please select a file');
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    uploadButton.disabled = true;
    progressContainer.style.display = 'block';
    statusMessage.style.display = 'none';
    
    progressBar.style.width = '0%';
    progressText.textContent = '0%';
    
    try {
        if (uploadXHR) {
            uploadXHR.abort(); 
        }
        const xhr = uploadXHR = new XMLHttpRequest();
        xhr.open('POST', window.location.origin + '/upload', true);
        
        xhr.upload.onprogress = (event) => {
            const percent = (event.loaded / event.total) * 100;
            progressBar.style.width = percent + '%';
            progressText.textContent = Math.round(percent) + '%';
        };
        
        xhr.upload.onerror = function() {
            console.error('Upload failed:', xhr.status, xhr.statusText);
            statusMessage.textContent = 'Upload failed. Please check your connection and try again.';
            statusMessage.style.backgroundColor = '#f2dede';
            statusMessage.style.display = 'block';
            uploadButton.disabled = false;
            uploadXHR = null;
        };
        
        xhr.onload = function() {
            if (xhr.status === 200) {
                const fileId = xhr.responseText;
                document.getElementById('fileIdDisplay').textContent = `File ID: ${fileId}`;
                document.getElementById('uploadResult').style.display = 'block';
                document.getElementById('fileInput').value = ''; 
                statusMessage.textContent = 'Upload successful!';
                statusMessage.style.backgroundColor = '#dff0d8';
                statusMessage.style.display = 'block';
                startCountdown();
            } else {
                console.error('Server returned error:', xhr.status, xhr.responseText);
                statusMessage.textContent = `Upload failed: Server returned ${xhr.status}`;
                statusMessage.style.backgroundColor = '#f2dede';
                statusMessage.style.display = 'block';
            }
            uploadXHR = null;
        };
        
        xhr.onerror = function() {
            console.error('Network error occurred');
            statusMessage.textContent = 'Network error occurred. Please check your connection.';
            statusMessage.style.backgroundColor = '#f2dede';
            statusMessage.style.display = 'block';
            uploadXHR = null;
        };
        
        xhr.send(formData);
    } catch (error) {
        console.error('Upload error:', error);
        statusMessage.textContent = 'Error uploading file: ' + error.message;
        statusMessage.style.backgroundColor = '#f2dede';
        statusMessage.style.display = 'block';
    } finally {
        uploadButton.disabled = false;
        progressContainer.style.display = 'none';
    }
}

function startCountdown() {
    let timeLeft = 600; 
    const countdownElement = document.getElementById('countdown');
    
    const timer = setInterval(() => {
        const minutes = Math.floor(timeLeft / 60);
        const seconds = timeLeft % 60;
        countdownElement.textContent = 
            `Expires in: ${minutes}:${seconds.toString().padStart(2, '0')}`;
        
        if (timeLeft <= 0) {
            clearInterval(timer);
            countdownElement.textContent = 'File has expired';
            document.getElementById('fileIdDisplay').textContent = '';
        }
        timeLeft--;
    }, 1000);
}
