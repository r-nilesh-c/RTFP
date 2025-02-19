// /src/main/resources/static/fileTransfer.js
function getServerUrl() {
    const serverAddress = document.getElementById('serverAddress').value.trim();
    return serverAddress || window.location.origin;
}

async function uploadFile() {
    const fileInput = document.getElementById('fileInput');
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    try {
        const serverUrl = getServerUrl();
        const response = await fetch(`${serverUrl}/upload`, {
            method: 'POST',
            body: formData
        });

        const result = await response.json();
        
        if (!response.ok) {
            throw new Error(result.error || 'Upload failed');
        }

        const fileId = result.fileId;
        const statusMessage = document.getElementById('statusMessage');
        statusMessage.textContent = 'File uploaded successfully. File ID: ' + fileId;
        statusMessage.style.backgroundColor = '#dff0d8';
        statusMessage.style.display = 'block';
    } catch (error) {
        console.error('Upload error:', error);
        const statusMessage = document.getElementById('statusMessage');
        statusMessage.textContent = error.message;
        statusMessage.style.backgroundColor = '#f2dede';
        statusMessage.style.display = 'block';
    }
}

async function downloadFile() {
    const fileId = document.getElementById('fileId').value;
    if (!fileId) {
        alert('Please enter a file ID');
        return;
    }

    try {
        const serverUrl = getServerUrl();
        const response = await fetch(`${serverUrl}/download/${fileId}`);
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || 'Download failed');
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const anchorElement = document.createElement('a');
        anchorElement.href = url;
        anchorElement.download = fileId; 
        document.body.appendChild(anchorElement);
        anchorElement.click();
        anchorElement.remove();
        window.URL.revokeObjectURL(url);
    } catch (error) {
        alert(error.message);
    }
}
