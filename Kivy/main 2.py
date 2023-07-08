import kivy

from kivy.app import App
from kivy.uix.label import Label
from kivy.uix.gridlayout import GridLayout
from kivy.uix.textinput import TextInput
from kivy.uix.button import Button
from kivy.uix.widget import Widget
from kivy.properties import ObjectProperty


# Criando uma classe para o aplicativo, que herda da classe "App" do Kivy.
class MyGrid(Widget):
    username = ObjectProperty(None)
    password = ObjectProperty(None)
    pass

    def btn_login(self):
        try:
            if self.username.text == "" or self.password.text == "":
                print("Preencha todos os campos!")
                self.username.text = ""
                self.password.text = ""
            else:
                print("Username: ", self.username.text, "Password: ", self.password.text)
                self.username.text = ""
                self.password.text = ""
        except Exception as e:
            print("Ocorreu algum erro durante a execução: " + str(e))
            self.username.text = ""
            self.password.text = ""

# O nome do arquivo ".kv" deve ser o mesmo nome da classe, em minúsculo, e sem o "App" no final. Exemplos:
# MyApp -> my.kv
# kodinxApp -> kodinx.kv
# whateverName -> whatevername.kv

class MyApp(App):
    def build(self):
        return MyGrid()


if __name__ == "__main__":
    MyApp().run()

