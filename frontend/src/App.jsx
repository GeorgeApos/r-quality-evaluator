import React, { useState } from 'react';
import axios from 'axios';

const App = () => {
    const [file, setFile] = useState(null);
    const [fileName, setFileName] = useState('');
    const handleFileChange = (event) => {
        setFile(event.target.files[0]);
        setFileName(event.target.files[0].name);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        const formData = new FormData();
        formData.append('file', file);

        try {
            await axios.post('http://localhost:8080/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            alert('File uploaded successfully!');
        } catch (error) {
            console.log(error);
            alert('Error uploading file!');
        }
    };

    return (
        <div>
            <form onSubmit={handleSubmit}>
                <div>
                    <input type="file" onChange={handleFileChange} />
                </div>
                <div>
                    <button type="submit">Upload</button>
                </div>
            </form>
        </div>
    );
};

export default App;
