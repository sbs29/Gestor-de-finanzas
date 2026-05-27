type WelcomeProps = {
    name: string,
    age: number
}

function Welcome({ name, age }: WelcomeProps) {
    return (
        <>
            <h2>Bienvenido {name}</h2>
            <p> Edad: {age}</p>
        </>
    )
}

export default Welcome