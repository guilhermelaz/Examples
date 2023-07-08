import kivy

from kivy.app import App
from kivy.uix.label import Label
from kivy.uix.gridlayout import GridLayout
from kivy.uix.textinput import TextInput
from kivy.uix.button import Button

# Criando uma classe para o aplicativo, que herda da classe "App" do Kivy.
class MyGrid(GridLayout):
    def __init__(self, **kwargs):
        super(MyGrid, self).__init__(**kwargs)
        self.cols = 1  # Definindo o número de colunas da grid principal do app.

        self.inside = GridLayout()  # Criando uma grid para ser adicionada na grid principal (nome da grid = inside).
        self.inside.cols = 2  # A nova grid terá 2 colunas.

        self.inside.add_widget(Label(text="Nome:"))  # Adicionando um label na grid inside.
        self.username = TextInput(multiline=False)
        self.inside.add_widget(self.username)  # Adicionando um text input na grid inside.

        self.inside.add_widget(Label(text="Senha:"))
        self.senha = TextInput(multiline=False)
        self.inside.add_widget(self.senha)

        self.add_widget(self.inside)  # Adicionando a grid inside na grid principal.

        self.submit = Button(text="Enviar", font_size=40)  # Criando um botão.
        self.submit.bind(on_press=self.press)  # Quando o botão for pressionado, a função "press" será executada.
        self.add_widget(self.submit)  # Adicionando um botão na grid principal.

    def press(self, instance):
        username = self.username.text  # Pegando o texto do text input.
        senha = self.senha.text  # Pegando o texto do text input.

        print(f"Nome: {username} - Senha: {senha}")

        self.username.text = ""  # Limpando o text input.
        self.senha.text = ""


class Myapp(App):
    def build(self):
        return MyGrid()


if __name__ == "__main__":
    Myapp().run()

